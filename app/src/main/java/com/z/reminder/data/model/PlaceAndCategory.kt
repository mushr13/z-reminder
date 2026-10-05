package com.z.reminder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PlaceType {
    OFFICE,
    HOME,
    OTHER
}

@Entity(tableName = "places")
data class Place(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radiusMeters: Float = 150f,
    val wifiSsids: String = "", // Comma-separated SSIDs / BSSIDs
    val type: PlaceType = PlaceType.OTHER,
    val isTrackingEnabled: Boolean = true
)

@Entity(tableName = "categories")
data class ReminderCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorHex: String,
    val iconName: String = "default"
)
