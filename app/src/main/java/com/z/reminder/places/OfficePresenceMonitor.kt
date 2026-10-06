package com.z.reminder.places

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.os.Build
import com.z.reminder.data.db.PlaceDao
import com.z.reminder.data.db.ReminderDao
import com.z.reminder.data.model.PlaceType
import com.z.reminder.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * Monitors Wi-Fi network transitions to drive the OfficeStateManager without battery drain.
 */
class OfficePresenceMonitor(
    private val context: Context,
    private val placeDao: PlaceDao,
    private val reminderDao: ReminderDao,
    private val notificationHelper: NotificationHelper,
    val stateManager: OfficeStateManager = OfficeStateManager()
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val scope = CoroutineScope(Dispatchers.IO)
    private var isMonitoring = false

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            checkCurrentNetwork(network)
        }

        override fun onCapabilitiesChanged(network: Network, capabilities: NetworkCapabilities) {
            checkCurrentNetwork(network, capabilities)
        }

        override fun onLost(network: Network) {
            val now = System.currentTimeMillis()
            val event = stateManager.onPresenceLost(now)
            // If departure confirmed, nothing needed; status is reset.
        }
    }

    fun startMonitoring() {
        if (isMonitoring) return
        try {
            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
            isMonitoring = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopMonitoring() {
        if (!isMonitoring) return
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            isMonitoring = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkCurrentNetwork(network: Network, capabilities: NetworkCapabilities? = null) {
        scope.launch {
            val caps = capabilities ?: connectivityManager.getNetworkCapabilities(network) ?: return@launch
            if (!caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                // Not on Wi-Fi (e.g. cellular data) -> Presence lost
                stateManager.onPresenceLost(System.currentTimeMillis())
                return@launch
            }

            var currentSsid: String? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val wifiInfo = caps.transportInfo as? WifiInfo
                currentSsid = wifiInfo?.ssid?.trim('"')
            }
            if (currentSsid.isNullOrBlank() || currentSsid == "<unknown ssid>") {
                @Suppress("DEPRECATION")
                val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? android.net.wifi.WifiManager
                currentSsid = wifiManager?.connectionInfo?.ssid?.trim('"')
            }

            // If SSID cannot be identified, never falsely trigger office presence!
            if (currentSsid.isNullOrBlank() || currentSsid == "<unknown ssid>") {
                return@launch
            }

            val places = placeDao.getAllPlaces().firstOrNull() ?: emptyList()
            val officePlace = places.firstOrNull { it.type == PlaceType.OFFICE && it.isTrackingEnabled }
            val homePlace = places.firstOrNull { it.type == PlaceType.HOME && it.isTrackingEnabled }

            val officeSsids = officePlace?.wifiSsids?.split(",")?.map { it.trim().trim('"') }?.filter { it.isNotBlank() } ?: emptyList()
            val homeSsids = homePlace?.wifiSsids?.split(",")?.map { it.trim().trim('"') }?.filter { it.isNotBlank() } ?: emptyList()

            val isOfficeNetwork = officeSsids.any { it.equals(currentSsid, ignoreCase = true) }
            val isHomeNetwork = homeSsids.any { it.equals(currentSsid, ignoreCase = true) }

            val now = System.currentTimeMillis()
            if (isHomeNetwork) {
                // At home: immediately reset office presence so office alarms NEVER fire at home
                stateManager.reset()
            } else if (isOfficeNetwork) {
                // Confirmed at office Wi-Fi
                val event = stateManager.onPresenceDetected(now)
                handleStateEvent(event, officePlace?.name ?: "Office")
            } else {
                // Unknown Wi-Fi
                stateManager.onPresenceLost(now)
            }
        }
    }

    private suspend fun handleStateEvent(event: OfficeStateManager.Event, placeName: String) {
        when (event) {
            is OfficeStateManager.Event.ArrivalDebouncePassed -> {
                // User has confirmed arrival at office (> 3 minutes)
                val pendingReminders = reminderDao.getAllActiveReminders().firstOrNull() ?: emptyList()
                notificationHelper.showOfficeArrivalNotification(placeName, pendingReminders.size)
            }
            is OfficeStateManager.Event.Trigger60MinCheckIn -> {
                // User has been at office for 60 continuous minutes
                notificationHelper.showOfficeCheckInNotification(placeName)
            }
            else -> {}
        }
    }
}
