package com.z.reminder.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.z.reminder.data.model.Place;
import com.z.reminder.data.model.PlaceType;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PlaceDao_Impl implements PlaceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Place> __insertionAdapterOfPlace;

  private final EntityDeletionOrUpdateAdapter<Place> __deletionAdapterOfPlace;

  private final EntityDeletionOrUpdateAdapter<Place> __updateAdapterOfPlace;

  public PlaceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPlace = new EntityInsertionAdapter<Place>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `places` (`id`,`name`,`latitude`,`longitude`,`radiusMeters`,`wifiSsids`,`type`,`isTrackingEnabled`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Place entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getLatitude() == null) {
          statement.bindNull(3);
        } else {
          statement.bindDouble(3, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.getLongitude());
        }
        statement.bindDouble(5, entity.getRadiusMeters());
        statement.bindString(6, entity.getWifiSsids());
        statement.bindString(7, __PlaceType_enumToString(entity.getType()));
        final int _tmp = entity.isTrackingEnabled() ? 1 : 0;
        statement.bindLong(8, _tmp);
      }
    };
    this.__deletionAdapterOfPlace = new EntityDeletionOrUpdateAdapter<Place>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `places` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Place entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfPlace = new EntityDeletionOrUpdateAdapter<Place>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `places` SET `id` = ?,`name` = ?,`latitude` = ?,`longitude` = ?,`radiusMeters` = ?,`wifiSsids` = ?,`type` = ?,`isTrackingEnabled` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Place entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getLatitude() == null) {
          statement.bindNull(3);
        } else {
          statement.bindDouble(3, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.getLongitude());
        }
        statement.bindDouble(5, entity.getRadiusMeters());
        statement.bindString(6, entity.getWifiSsids());
        statement.bindString(7, __PlaceType_enumToString(entity.getType()));
        final int _tmp = entity.isTrackingEnabled() ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.getId());
      }
    };
  }

  @Override
  public Object insertPlace(final Place place, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPlace.insertAndReturnId(place);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePlace(final Place place, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfPlace.handle(place);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePlace(final Place place, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPlace.handle(place);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Place>> getAllPlaces() {
    final String _sql = "SELECT * FROM places ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"places"}, new Callable<List<Place>>() {
      @Override
      @NonNull
      public List<Place> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfRadiusMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "radiusMeters");
          final int _cursorIndexOfWifiSsids = CursorUtil.getColumnIndexOrThrow(_cursor, "wifiSsids");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfIsTrackingEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "isTrackingEnabled");
          final List<Place> _result = new ArrayList<Place>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Place _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final float _tmpRadiusMeters;
            _tmpRadiusMeters = _cursor.getFloat(_cursorIndexOfRadiusMeters);
            final String _tmpWifiSsids;
            _tmpWifiSsids = _cursor.getString(_cursorIndexOfWifiSsids);
            final PlaceType _tmpType;
            _tmpType = __PlaceType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final boolean _tmpIsTrackingEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTrackingEnabled);
            _tmpIsTrackingEnabled = _tmp != 0;
            _item = new Place(_tmpId,_tmpName,_tmpLatitude,_tmpLongitude,_tmpRadiusMeters,_tmpWifiSsids,_tmpType,_tmpIsTrackingEnabled);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getPlaceById(final long id, final Continuation<? super Place> $completion) {
    final String _sql = "SELECT * FROM places WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Place>() {
      @Override
      @Nullable
      public Place call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfRadiusMeters = CursorUtil.getColumnIndexOrThrow(_cursor, "radiusMeters");
          final int _cursorIndexOfWifiSsids = CursorUtil.getColumnIndexOrThrow(_cursor, "wifiSsids");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfIsTrackingEnabled = CursorUtil.getColumnIndexOrThrow(_cursor, "isTrackingEnabled");
          final Place _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final float _tmpRadiusMeters;
            _tmpRadiusMeters = _cursor.getFloat(_cursorIndexOfRadiusMeters);
            final String _tmpWifiSsids;
            _tmpWifiSsids = _cursor.getString(_cursorIndexOfWifiSsids);
            final PlaceType _tmpType;
            _tmpType = __PlaceType_stringToEnum(_cursor.getString(_cursorIndexOfType));
            final boolean _tmpIsTrackingEnabled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsTrackingEnabled);
            _tmpIsTrackingEnabled = _tmp != 0;
            _result = new Place(_tmpId,_tmpName,_tmpLatitude,_tmpLongitude,_tmpRadiusMeters,_tmpWifiSsids,_tmpType,_tmpIsTrackingEnabled);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __PlaceType_enumToString(@NonNull final PlaceType _value) {
    switch (_value) {
      case OFFICE: return "OFFICE";
      case HOME: return "HOME";
      case OTHER: return "OTHER";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private PlaceType __PlaceType_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "OFFICE": return PlaceType.OFFICE;
      case "HOME": return PlaceType.HOME;
      case "OTHER": return PlaceType.OTHER;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
