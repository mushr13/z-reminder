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
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.z.reminder.data.model.Reminder;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class ReminderDao_Impl implements ReminderDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Reminder> __insertionAdapterOfReminder;

  private final EntityDeletionOrUpdateAdapter<Reminder> __deletionAdapterOfReminder;

  private final EntityDeletionOrUpdateAdapter<Reminder> __updateAdapterOfReminder;

  private final SharedSQLiteStatement __preparedStmtOfDeleteReminderById;

  private final SharedSQLiteStatement __preparedStmtOfMarkCompleted;

  private final SharedSQLiteStatement __preparedStmtOfRestoreCompleted;

  private final SharedSQLiteStatement __preparedStmtOfSnoozeReminder;

  public ReminderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfReminder = new EntityInsertionAdapter<Reminder>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reminders` (`id`,`title`,`notes`,`dueAt`,`timezoneId`,`repeatUnit`,`repeatInterval`,`repeatWeekdaysMask`,`repeatMode`,`repeatEndAt`,`priority`,`categoryId`,`placeId`,`placeTrigger`,`nagIntervalMinutes`,`alertStyle`,`backgroundId`,`status`,`snoozedUntil`,`createdAt`,`completedAt`,`snoozeCount`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Reminder entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getNotes());
        statement.bindLong(4, entity.getDueAt());
        statement.bindString(5, entity.getTimezoneId());
        if (entity.getRepeatUnit() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getRepeatUnit());
        }
        statement.bindLong(7, entity.getRepeatInterval());
        statement.bindLong(8, entity.getRepeatWeekdaysMask());
        statement.bindString(9, entity.getRepeatMode());
        if (entity.getRepeatEndAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getRepeatEndAt());
        }
        statement.bindString(11, entity.getPriority());
        if (entity.getCategoryId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getCategoryId());
        }
        if (entity.getPlaceId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getPlaceId());
        }
        statement.bindString(14, entity.getPlaceTrigger());
        if (entity.getNagIntervalMinutes() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getNagIntervalMinutes());
        }
        statement.bindString(16, entity.getAlertStyle());
        if (entity.getBackgroundId() == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.getBackgroundId());
        }
        statement.bindString(18, entity.getStatus());
        if (entity.getSnoozedUntil() == null) {
          statement.bindNull(19);
        } else {
          statement.bindLong(19, entity.getSnoozedUntil());
        }
        statement.bindLong(20, entity.getCreatedAt());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(21);
        } else {
          statement.bindLong(21, entity.getCompletedAt());
        }
        statement.bindLong(22, entity.getSnoozeCount());
      }
    };
    this.__deletionAdapterOfReminder = new EntityDeletionOrUpdateAdapter<Reminder>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `reminders` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Reminder entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfReminder = new EntityDeletionOrUpdateAdapter<Reminder>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `reminders` SET `id` = ?,`title` = ?,`notes` = ?,`dueAt` = ?,`timezoneId` = ?,`repeatUnit` = ?,`repeatInterval` = ?,`repeatWeekdaysMask` = ?,`repeatMode` = ?,`repeatEndAt` = ?,`priority` = ?,`categoryId` = ?,`placeId` = ?,`placeTrigger` = ?,`nagIntervalMinutes` = ?,`alertStyle` = ?,`backgroundId` = ?,`status` = ?,`snoozedUntil` = ?,`createdAt` = ?,`completedAt` = ?,`snoozeCount` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Reminder entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getNotes());
        statement.bindLong(4, entity.getDueAt());
        statement.bindString(5, entity.getTimezoneId());
        if (entity.getRepeatUnit() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getRepeatUnit());
        }
        statement.bindLong(7, entity.getRepeatInterval());
        statement.bindLong(8, entity.getRepeatWeekdaysMask());
        statement.bindString(9, entity.getRepeatMode());
        if (entity.getRepeatEndAt() == null) {
          statement.bindNull(10);
        } else {
          statement.bindLong(10, entity.getRepeatEndAt());
        }
        statement.bindString(11, entity.getPriority());
        if (entity.getCategoryId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getCategoryId());
        }
        if (entity.getPlaceId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getPlaceId());
        }
        statement.bindString(14, entity.getPlaceTrigger());
        if (entity.getNagIntervalMinutes() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getNagIntervalMinutes());
        }
        statement.bindString(16, entity.getAlertStyle());
        if (entity.getBackgroundId() == null) {
          statement.bindNull(17);
        } else {
          statement.bindString(17, entity.getBackgroundId());
        }
        statement.bindString(18, entity.getStatus());
        if (entity.getSnoozedUntil() == null) {
          statement.bindNull(19);
        } else {
          statement.bindLong(19, entity.getSnoozedUntil());
        }
        statement.bindLong(20, entity.getCreatedAt());
        if (entity.getCompletedAt() == null) {
          statement.bindNull(21);
        } else {
          statement.bindLong(21, entity.getCompletedAt());
        }
        statement.bindLong(22, entity.getSnoozeCount());
        statement.bindLong(23, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteReminderById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM reminders WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkCompleted = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE reminders SET status = 'COMPLETED', completedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRestoreCompleted = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE reminders SET status = 'SCHEDULED', completedAt = null WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSnoozeReminder = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE reminders SET status = 'SNOOZED', snoozedUntil = ?, snoozeCount = snoozeCount + 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertReminder(final Reminder reminder,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfReminder.insertAndReturnId(reminder);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteReminder(final Reminder reminder,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfReminder.handle(reminder);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateReminder(final Reminder reminder,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfReminder.handle(reminder);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteReminderById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteReminderById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteReminderById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markCompleted(final long id, final long completedAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkCompleted.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, completedAt);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkCompleted.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object restoreCompleted(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRestoreCompleted.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfRestoreCompleted.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object snoozeReminder(final long id, final long snoozedUntil,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSnoozeReminder.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, snoozedUntil);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfSnoozeReminder.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Reminder>> getAllActiveReminders() {
    final String _sql = "SELECT * FROM reminders WHERE status != 'COMPLETED' ORDER BY dueAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reminders"}, new Callable<List<Reminder>>() {
      @Override
      @NonNull
      public List<Reminder> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfDueAt = CursorUtil.getColumnIndexOrThrow(_cursor, "dueAt");
          final int _cursorIndexOfTimezoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "timezoneId");
          final int _cursorIndexOfRepeatUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatUnit");
          final int _cursorIndexOfRepeatInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatInterval");
          final int _cursorIndexOfRepeatWeekdaysMask = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatWeekdaysMask");
          final int _cursorIndexOfRepeatMode = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatMode");
          final int _cursorIndexOfRepeatEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatEndAt");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfPlaceId = CursorUtil.getColumnIndexOrThrow(_cursor, "placeId");
          final int _cursorIndexOfPlaceTrigger = CursorUtil.getColumnIndexOrThrow(_cursor, "placeTrigger");
          final int _cursorIndexOfNagIntervalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "nagIntervalMinutes");
          final int _cursorIndexOfAlertStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "alertStyle");
          final int _cursorIndexOfBackgroundId = CursorUtil.getColumnIndexOrThrow(_cursor, "backgroundId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfSnoozedUntil = CursorUtil.getColumnIndexOrThrow(_cursor, "snoozedUntil");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfSnoozeCount = CursorUtil.getColumnIndexOrThrow(_cursor, "snoozeCount");
          final List<Reminder> _result = new ArrayList<Reminder>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Reminder _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpDueAt;
            _tmpDueAt = _cursor.getLong(_cursorIndexOfDueAt);
            final String _tmpTimezoneId;
            _tmpTimezoneId = _cursor.getString(_cursorIndexOfTimezoneId);
            final String _tmpRepeatUnit;
            if (_cursor.isNull(_cursorIndexOfRepeatUnit)) {
              _tmpRepeatUnit = null;
            } else {
              _tmpRepeatUnit = _cursor.getString(_cursorIndexOfRepeatUnit);
            }
            final int _tmpRepeatInterval;
            _tmpRepeatInterval = _cursor.getInt(_cursorIndexOfRepeatInterval);
            final int _tmpRepeatWeekdaysMask;
            _tmpRepeatWeekdaysMask = _cursor.getInt(_cursorIndexOfRepeatWeekdaysMask);
            final String _tmpRepeatMode;
            _tmpRepeatMode = _cursor.getString(_cursorIndexOfRepeatMode);
            final Long _tmpRepeatEndAt;
            if (_cursor.isNull(_cursorIndexOfRepeatEndAt)) {
              _tmpRepeatEndAt = null;
            } else {
              _tmpRepeatEndAt = _cursor.getLong(_cursorIndexOfRepeatEndAt);
            }
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpPlaceId;
            if (_cursor.isNull(_cursorIndexOfPlaceId)) {
              _tmpPlaceId = null;
            } else {
              _tmpPlaceId = _cursor.getLong(_cursorIndexOfPlaceId);
            }
            final String _tmpPlaceTrigger;
            _tmpPlaceTrigger = _cursor.getString(_cursorIndexOfPlaceTrigger);
            final Integer _tmpNagIntervalMinutes;
            if (_cursor.isNull(_cursorIndexOfNagIntervalMinutes)) {
              _tmpNagIntervalMinutes = null;
            } else {
              _tmpNagIntervalMinutes = _cursor.getInt(_cursorIndexOfNagIntervalMinutes);
            }
            final String _tmpAlertStyle;
            _tmpAlertStyle = _cursor.getString(_cursorIndexOfAlertStyle);
            final String _tmpBackgroundId;
            if (_cursor.isNull(_cursorIndexOfBackgroundId)) {
              _tmpBackgroundId = null;
            } else {
              _tmpBackgroundId = _cursor.getString(_cursorIndexOfBackgroundId);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpSnoozedUntil;
            if (_cursor.isNull(_cursorIndexOfSnoozedUntil)) {
              _tmpSnoozedUntil = null;
            } else {
              _tmpSnoozedUntil = _cursor.getLong(_cursorIndexOfSnoozedUntil);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpSnoozeCount;
            _tmpSnoozeCount = _cursor.getInt(_cursorIndexOfSnoozeCount);
            _item = new Reminder(_tmpId,_tmpTitle,_tmpNotes,_tmpDueAt,_tmpTimezoneId,_tmpRepeatUnit,_tmpRepeatInterval,_tmpRepeatWeekdaysMask,_tmpRepeatMode,_tmpRepeatEndAt,_tmpPriority,_tmpCategoryId,_tmpPlaceId,_tmpPlaceTrigger,_tmpNagIntervalMinutes,_tmpAlertStyle,_tmpBackgroundId,_tmpStatus,_tmpSnoozedUntil,_tmpCreatedAt,_tmpCompletedAt,_tmpSnoozeCount);
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
  public Flow<List<Reminder>> getCompletedReminders() {
    final String _sql = "SELECT * FROM reminders WHERE status = 'COMPLETED' ORDER BY completedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reminders"}, new Callable<List<Reminder>>() {
      @Override
      @NonNull
      public List<Reminder> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfDueAt = CursorUtil.getColumnIndexOrThrow(_cursor, "dueAt");
          final int _cursorIndexOfTimezoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "timezoneId");
          final int _cursorIndexOfRepeatUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatUnit");
          final int _cursorIndexOfRepeatInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatInterval");
          final int _cursorIndexOfRepeatWeekdaysMask = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatWeekdaysMask");
          final int _cursorIndexOfRepeatMode = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatMode");
          final int _cursorIndexOfRepeatEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatEndAt");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfPlaceId = CursorUtil.getColumnIndexOrThrow(_cursor, "placeId");
          final int _cursorIndexOfPlaceTrigger = CursorUtil.getColumnIndexOrThrow(_cursor, "placeTrigger");
          final int _cursorIndexOfNagIntervalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "nagIntervalMinutes");
          final int _cursorIndexOfAlertStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "alertStyle");
          final int _cursorIndexOfBackgroundId = CursorUtil.getColumnIndexOrThrow(_cursor, "backgroundId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfSnoozedUntil = CursorUtil.getColumnIndexOrThrow(_cursor, "snoozedUntil");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfSnoozeCount = CursorUtil.getColumnIndexOrThrow(_cursor, "snoozeCount");
          final List<Reminder> _result = new ArrayList<Reminder>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Reminder _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpDueAt;
            _tmpDueAt = _cursor.getLong(_cursorIndexOfDueAt);
            final String _tmpTimezoneId;
            _tmpTimezoneId = _cursor.getString(_cursorIndexOfTimezoneId);
            final String _tmpRepeatUnit;
            if (_cursor.isNull(_cursorIndexOfRepeatUnit)) {
              _tmpRepeatUnit = null;
            } else {
              _tmpRepeatUnit = _cursor.getString(_cursorIndexOfRepeatUnit);
            }
            final int _tmpRepeatInterval;
            _tmpRepeatInterval = _cursor.getInt(_cursorIndexOfRepeatInterval);
            final int _tmpRepeatWeekdaysMask;
            _tmpRepeatWeekdaysMask = _cursor.getInt(_cursorIndexOfRepeatWeekdaysMask);
            final String _tmpRepeatMode;
            _tmpRepeatMode = _cursor.getString(_cursorIndexOfRepeatMode);
            final Long _tmpRepeatEndAt;
            if (_cursor.isNull(_cursorIndexOfRepeatEndAt)) {
              _tmpRepeatEndAt = null;
            } else {
              _tmpRepeatEndAt = _cursor.getLong(_cursorIndexOfRepeatEndAt);
            }
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpPlaceId;
            if (_cursor.isNull(_cursorIndexOfPlaceId)) {
              _tmpPlaceId = null;
            } else {
              _tmpPlaceId = _cursor.getLong(_cursorIndexOfPlaceId);
            }
            final String _tmpPlaceTrigger;
            _tmpPlaceTrigger = _cursor.getString(_cursorIndexOfPlaceTrigger);
            final Integer _tmpNagIntervalMinutes;
            if (_cursor.isNull(_cursorIndexOfNagIntervalMinutes)) {
              _tmpNagIntervalMinutes = null;
            } else {
              _tmpNagIntervalMinutes = _cursor.getInt(_cursorIndexOfNagIntervalMinutes);
            }
            final String _tmpAlertStyle;
            _tmpAlertStyle = _cursor.getString(_cursorIndexOfAlertStyle);
            final String _tmpBackgroundId;
            if (_cursor.isNull(_cursorIndexOfBackgroundId)) {
              _tmpBackgroundId = null;
            } else {
              _tmpBackgroundId = _cursor.getString(_cursorIndexOfBackgroundId);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpSnoozedUntil;
            if (_cursor.isNull(_cursorIndexOfSnoozedUntil)) {
              _tmpSnoozedUntil = null;
            } else {
              _tmpSnoozedUntil = _cursor.getLong(_cursorIndexOfSnoozedUntil);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpSnoozeCount;
            _tmpSnoozeCount = _cursor.getInt(_cursorIndexOfSnoozeCount);
            _item = new Reminder(_tmpId,_tmpTitle,_tmpNotes,_tmpDueAt,_tmpTimezoneId,_tmpRepeatUnit,_tmpRepeatInterval,_tmpRepeatWeekdaysMask,_tmpRepeatMode,_tmpRepeatEndAt,_tmpPriority,_tmpCategoryId,_tmpPlaceId,_tmpPlaceTrigger,_tmpNagIntervalMinutes,_tmpAlertStyle,_tmpBackgroundId,_tmpStatus,_tmpSnoozedUntil,_tmpCreatedAt,_tmpCompletedAt,_tmpSnoozeCount);
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
  public Object getReminderById(final long id, final Continuation<? super Reminder> $completion) {
    final String _sql = "SELECT * FROM reminders WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Reminder>() {
      @Override
      @Nullable
      public Reminder call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfDueAt = CursorUtil.getColumnIndexOrThrow(_cursor, "dueAt");
          final int _cursorIndexOfTimezoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "timezoneId");
          final int _cursorIndexOfRepeatUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatUnit");
          final int _cursorIndexOfRepeatInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatInterval");
          final int _cursorIndexOfRepeatWeekdaysMask = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatWeekdaysMask");
          final int _cursorIndexOfRepeatMode = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatMode");
          final int _cursorIndexOfRepeatEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatEndAt");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfPlaceId = CursorUtil.getColumnIndexOrThrow(_cursor, "placeId");
          final int _cursorIndexOfPlaceTrigger = CursorUtil.getColumnIndexOrThrow(_cursor, "placeTrigger");
          final int _cursorIndexOfNagIntervalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "nagIntervalMinutes");
          final int _cursorIndexOfAlertStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "alertStyle");
          final int _cursorIndexOfBackgroundId = CursorUtil.getColumnIndexOrThrow(_cursor, "backgroundId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfSnoozedUntil = CursorUtil.getColumnIndexOrThrow(_cursor, "snoozedUntil");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfSnoozeCount = CursorUtil.getColumnIndexOrThrow(_cursor, "snoozeCount");
          final Reminder _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpDueAt;
            _tmpDueAt = _cursor.getLong(_cursorIndexOfDueAt);
            final String _tmpTimezoneId;
            _tmpTimezoneId = _cursor.getString(_cursorIndexOfTimezoneId);
            final String _tmpRepeatUnit;
            if (_cursor.isNull(_cursorIndexOfRepeatUnit)) {
              _tmpRepeatUnit = null;
            } else {
              _tmpRepeatUnit = _cursor.getString(_cursorIndexOfRepeatUnit);
            }
            final int _tmpRepeatInterval;
            _tmpRepeatInterval = _cursor.getInt(_cursorIndexOfRepeatInterval);
            final int _tmpRepeatWeekdaysMask;
            _tmpRepeatWeekdaysMask = _cursor.getInt(_cursorIndexOfRepeatWeekdaysMask);
            final String _tmpRepeatMode;
            _tmpRepeatMode = _cursor.getString(_cursorIndexOfRepeatMode);
            final Long _tmpRepeatEndAt;
            if (_cursor.isNull(_cursorIndexOfRepeatEndAt)) {
              _tmpRepeatEndAt = null;
            } else {
              _tmpRepeatEndAt = _cursor.getLong(_cursorIndexOfRepeatEndAt);
            }
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpPlaceId;
            if (_cursor.isNull(_cursorIndexOfPlaceId)) {
              _tmpPlaceId = null;
            } else {
              _tmpPlaceId = _cursor.getLong(_cursorIndexOfPlaceId);
            }
            final String _tmpPlaceTrigger;
            _tmpPlaceTrigger = _cursor.getString(_cursorIndexOfPlaceTrigger);
            final Integer _tmpNagIntervalMinutes;
            if (_cursor.isNull(_cursorIndexOfNagIntervalMinutes)) {
              _tmpNagIntervalMinutes = null;
            } else {
              _tmpNagIntervalMinutes = _cursor.getInt(_cursorIndexOfNagIntervalMinutes);
            }
            final String _tmpAlertStyle;
            _tmpAlertStyle = _cursor.getString(_cursorIndexOfAlertStyle);
            final String _tmpBackgroundId;
            if (_cursor.isNull(_cursorIndexOfBackgroundId)) {
              _tmpBackgroundId = null;
            } else {
              _tmpBackgroundId = _cursor.getString(_cursorIndexOfBackgroundId);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpSnoozedUntil;
            if (_cursor.isNull(_cursorIndexOfSnoozedUntil)) {
              _tmpSnoozedUntil = null;
            } else {
              _tmpSnoozedUntil = _cursor.getLong(_cursorIndexOfSnoozedUntil);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpSnoozeCount;
            _tmpSnoozeCount = _cursor.getInt(_cursorIndexOfSnoozeCount);
            _result = new Reminder(_tmpId,_tmpTitle,_tmpNotes,_tmpDueAt,_tmpTimezoneId,_tmpRepeatUnit,_tmpRepeatInterval,_tmpRepeatWeekdaysMask,_tmpRepeatMode,_tmpRepeatEndAt,_tmpPriority,_tmpCategoryId,_tmpPlaceId,_tmpPlaceTrigger,_tmpNagIntervalMinutes,_tmpAlertStyle,_tmpBackgroundId,_tmpStatus,_tmpSnoozedUntil,_tmpCreatedAt,_tmpCompletedAt,_tmpSnoozeCount);
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

  @Override
  public Flow<List<Reminder>> getRemindersForDay(final long startOfDay, final long endOfDay) {
    final String _sql = "SELECT * FROM reminders WHERE dueAt BETWEEN ? AND ? AND status != 'COMPLETED' ORDER BY dueAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reminders"}, new Callable<List<Reminder>>() {
      @Override
      @NonNull
      public List<Reminder> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfDueAt = CursorUtil.getColumnIndexOrThrow(_cursor, "dueAt");
          final int _cursorIndexOfTimezoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "timezoneId");
          final int _cursorIndexOfRepeatUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatUnit");
          final int _cursorIndexOfRepeatInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatInterval");
          final int _cursorIndexOfRepeatWeekdaysMask = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatWeekdaysMask");
          final int _cursorIndexOfRepeatMode = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatMode");
          final int _cursorIndexOfRepeatEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatEndAt");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfPlaceId = CursorUtil.getColumnIndexOrThrow(_cursor, "placeId");
          final int _cursorIndexOfPlaceTrigger = CursorUtil.getColumnIndexOrThrow(_cursor, "placeTrigger");
          final int _cursorIndexOfNagIntervalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "nagIntervalMinutes");
          final int _cursorIndexOfAlertStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "alertStyle");
          final int _cursorIndexOfBackgroundId = CursorUtil.getColumnIndexOrThrow(_cursor, "backgroundId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfSnoozedUntil = CursorUtil.getColumnIndexOrThrow(_cursor, "snoozedUntil");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfSnoozeCount = CursorUtil.getColumnIndexOrThrow(_cursor, "snoozeCount");
          final List<Reminder> _result = new ArrayList<Reminder>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Reminder _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpDueAt;
            _tmpDueAt = _cursor.getLong(_cursorIndexOfDueAt);
            final String _tmpTimezoneId;
            _tmpTimezoneId = _cursor.getString(_cursorIndexOfTimezoneId);
            final String _tmpRepeatUnit;
            if (_cursor.isNull(_cursorIndexOfRepeatUnit)) {
              _tmpRepeatUnit = null;
            } else {
              _tmpRepeatUnit = _cursor.getString(_cursorIndexOfRepeatUnit);
            }
            final int _tmpRepeatInterval;
            _tmpRepeatInterval = _cursor.getInt(_cursorIndexOfRepeatInterval);
            final int _tmpRepeatWeekdaysMask;
            _tmpRepeatWeekdaysMask = _cursor.getInt(_cursorIndexOfRepeatWeekdaysMask);
            final String _tmpRepeatMode;
            _tmpRepeatMode = _cursor.getString(_cursorIndexOfRepeatMode);
            final Long _tmpRepeatEndAt;
            if (_cursor.isNull(_cursorIndexOfRepeatEndAt)) {
              _tmpRepeatEndAt = null;
            } else {
              _tmpRepeatEndAt = _cursor.getLong(_cursorIndexOfRepeatEndAt);
            }
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpPlaceId;
            if (_cursor.isNull(_cursorIndexOfPlaceId)) {
              _tmpPlaceId = null;
            } else {
              _tmpPlaceId = _cursor.getLong(_cursorIndexOfPlaceId);
            }
            final String _tmpPlaceTrigger;
            _tmpPlaceTrigger = _cursor.getString(_cursorIndexOfPlaceTrigger);
            final Integer _tmpNagIntervalMinutes;
            if (_cursor.isNull(_cursorIndexOfNagIntervalMinutes)) {
              _tmpNagIntervalMinutes = null;
            } else {
              _tmpNagIntervalMinutes = _cursor.getInt(_cursorIndexOfNagIntervalMinutes);
            }
            final String _tmpAlertStyle;
            _tmpAlertStyle = _cursor.getString(_cursorIndexOfAlertStyle);
            final String _tmpBackgroundId;
            if (_cursor.isNull(_cursorIndexOfBackgroundId)) {
              _tmpBackgroundId = null;
            } else {
              _tmpBackgroundId = _cursor.getString(_cursorIndexOfBackgroundId);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpSnoozedUntil;
            if (_cursor.isNull(_cursorIndexOfSnoozedUntil)) {
              _tmpSnoozedUntil = null;
            } else {
              _tmpSnoozedUntil = _cursor.getLong(_cursorIndexOfSnoozedUntil);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpSnoozeCount;
            _tmpSnoozeCount = _cursor.getInt(_cursorIndexOfSnoozeCount);
            _item = new Reminder(_tmpId,_tmpTitle,_tmpNotes,_tmpDueAt,_tmpTimezoneId,_tmpRepeatUnit,_tmpRepeatInterval,_tmpRepeatWeekdaysMask,_tmpRepeatMode,_tmpRepeatEndAt,_tmpPriority,_tmpCategoryId,_tmpPlaceId,_tmpPlaceTrigger,_tmpNagIntervalMinutes,_tmpAlertStyle,_tmpBackgroundId,_tmpStatus,_tmpSnoozedUntil,_tmpCreatedAt,_tmpCompletedAt,_tmpSnoozeCount);
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
  public Object getRemindersForDaySnapshot(final long startOfDay, final long endOfDay,
      final Continuation<? super List<Reminder>> $completion) {
    final String _sql = "SELECT * FROM reminders WHERE dueAt BETWEEN ? AND ? AND status != 'COMPLETED' ORDER BY dueAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startOfDay);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endOfDay);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Reminder>>() {
      @Override
      @NonNull
      public List<Reminder> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfDueAt = CursorUtil.getColumnIndexOrThrow(_cursor, "dueAt");
          final int _cursorIndexOfTimezoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "timezoneId");
          final int _cursorIndexOfRepeatUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatUnit");
          final int _cursorIndexOfRepeatInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatInterval");
          final int _cursorIndexOfRepeatWeekdaysMask = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatWeekdaysMask");
          final int _cursorIndexOfRepeatMode = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatMode");
          final int _cursorIndexOfRepeatEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatEndAt");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfPlaceId = CursorUtil.getColumnIndexOrThrow(_cursor, "placeId");
          final int _cursorIndexOfPlaceTrigger = CursorUtil.getColumnIndexOrThrow(_cursor, "placeTrigger");
          final int _cursorIndexOfNagIntervalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "nagIntervalMinutes");
          final int _cursorIndexOfAlertStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "alertStyle");
          final int _cursorIndexOfBackgroundId = CursorUtil.getColumnIndexOrThrow(_cursor, "backgroundId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfSnoozedUntil = CursorUtil.getColumnIndexOrThrow(_cursor, "snoozedUntil");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfSnoozeCount = CursorUtil.getColumnIndexOrThrow(_cursor, "snoozeCount");
          final List<Reminder> _result = new ArrayList<Reminder>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Reminder _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpDueAt;
            _tmpDueAt = _cursor.getLong(_cursorIndexOfDueAt);
            final String _tmpTimezoneId;
            _tmpTimezoneId = _cursor.getString(_cursorIndexOfTimezoneId);
            final String _tmpRepeatUnit;
            if (_cursor.isNull(_cursorIndexOfRepeatUnit)) {
              _tmpRepeatUnit = null;
            } else {
              _tmpRepeatUnit = _cursor.getString(_cursorIndexOfRepeatUnit);
            }
            final int _tmpRepeatInterval;
            _tmpRepeatInterval = _cursor.getInt(_cursorIndexOfRepeatInterval);
            final int _tmpRepeatWeekdaysMask;
            _tmpRepeatWeekdaysMask = _cursor.getInt(_cursorIndexOfRepeatWeekdaysMask);
            final String _tmpRepeatMode;
            _tmpRepeatMode = _cursor.getString(_cursorIndexOfRepeatMode);
            final Long _tmpRepeatEndAt;
            if (_cursor.isNull(_cursorIndexOfRepeatEndAt)) {
              _tmpRepeatEndAt = null;
            } else {
              _tmpRepeatEndAt = _cursor.getLong(_cursorIndexOfRepeatEndAt);
            }
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpPlaceId;
            if (_cursor.isNull(_cursorIndexOfPlaceId)) {
              _tmpPlaceId = null;
            } else {
              _tmpPlaceId = _cursor.getLong(_cursorIndexOfPlaceId);
            }
            final String _tmpPlaceTrigger;
            _tmpPlaceTrigger = _cursor.getString(_cursorIndexOfPlaceTrigger);
            final Integer _tmpNagIntervalMinutes;
            if (_cursor.isNull(_cursorIndexOfNagIntervalMinutes)) {
              _tmpNagIntervalMinutes = null;
            } else {
              _tmpNagIntervalMinutes = _cursor.getInt(_cursorIndexOfNagIntervalMinutes);
            }
            final String _tmpAlertStyle;
            _tmpAlertStyle = _cursor.getString(_cursorIndexOfAlertStyle);
            final String _tmpBackgroundId;
            if (_cursor.isNull(_cursorIndexOfBackgroundId)) {
              _tmpBackgroundId = null;
            } else {
              _tmpBackgroundId = _cursor.getString(_cursorIndexOfBackgroundId);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpSnoozedUntil;
            if (_cursor.isNull(_cursorIndexOfSnoozedUntil)) {
              _tmpSnoozedUntil = null;
            } else {
              _tmpSnoozedUntil = _cursor.getLong(_cursorIndexOfSnoozedUntil);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpSnoozeCount;
            _tmpSnoozeCount = _cursor.getInt(_cursorIndexOfSnoozeCount);
            _item = new Reminder(_tmpId,_tmpTitle,_tmpNotes,_tmpDueAt,_tmpTimezoneId,_tmpRepeatUnit,_tmpRepeatInterval,_tmpRepeatWeekdaysMask,_tmpRepeatMode,_tmpRepeatEndAt,_tmpPriority,_tmpCategoryId,_tmpPlaceId,_tmpPlaceTrigger,_tmpNagIntervalMinutes,_tmpAlertStyle,_tmpBackgroundId,_tmpStatus,_tmpSnoozedUntil,_tmpCreatedAt,_tmpCompletedAt,_tmpSnoozeCount);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Reminder>> getOverdueReminders(final long now) {
    final String _sql = "SELECT * FROM reminders WHERE dueAt < ? AND status != 'COMPLETED' ORDER BY dueAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, now);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reminders"}, new Callable<List<Reminder>>() {
      @Override
      @NonNull
      public List<Reminder> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfDueAt = CursorUtil.getColumnIndexOrThrow(_cursor, "dueAt");
          final int _cursorIndexOfTimezoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "timezoneId");
          final int _cursorIndexOfRepeatUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatUnit");
          final int _cursorIndexOfRepeatInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatInterval");
          final int _cursorIndexOfRepeatWeekdaysMask = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatWeekdaysMask");
          final int _cursorIndexOfRepeatMode = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatMode");
          final int _cursorIndexOfRepeatEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatEndAt");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfPlaceId = CursorUtil.getColumnIndexOrThrow(_cursor, "placeId");
          final int _cursorIndexOfPlaceTrigger = CursorUtil.getColumnIndexOrThrow(_cursor, "placeTrigger");
          final int _cursorIndexOfNagIntervalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "nagIntervalMinutes");
          final int _cursorIndexOfAlertStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "alertStyle");
          final int _cursorIndexOfBackgroundId = CursorUtil.getColumnIndexOrThrow(_cursor, "backgroundId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfSnoozedUntil = CursorUtil.getColumnIndexOrThrow(_cursor, "snoozedUntil");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfSnoozeCount = CursorUtil.getColumnIndexOrThrow(_cursor, "snoozeCount");
          final List<Reminder> _result = new ArrayList<Reminder>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Reminder _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpDueAt;
            _tmpDueAt = _cursor.getLong(_cursorIndexOfDueAt);
            final String _tmpTimezoneId;
            _tmpTimezoneId = _cursor.getString(_cursorIndexOfTimezoneId);
            final String _tmpRepeatUnit;
            if (_cursor.isNull(_cursorIndexOfRepeatUnit)) {
              _tmpRepeatUnit = null;
            } else {
              _tmpRepeatUnit = _cursor.getString(_cursorIndexOfRepeatUnit);
            }
            final int _tmpRepeatInterval;
            _tmpRepeatInterval = _cursor.getInt(_cursorIndexOfRepeatInterval);
            final int _tmpRepeatWeekdaysMask;
            _tmpRepeatWeekdaysMask = _cursor.getInt(_cursorIndexOfRepeatWeekdaysMask);
            final String _tmpRepeatMode;
            _tmpRepeatMode = _cursor.getString(_cursorIndexOfRepeatMode);
            final Long _tmpRepeatEndAt;
            if (_cursor.isNull(_cursorIndexOfRepeatEndAt)) {
              _tmpRepeatEndAt = null;
            } else {
              _tmpRepeatEndAt = _cursor.getLong(_cursorIndexOfRepeatEndAt);
            }
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpPlaceId;
            if (_cursor.isNull(_cursorIndexOfPlaceId)) {
              _tmpPlaceId = null;
            } else {
              _tmpPlaceId = _cursor.getLong(_cursorIndexOfPlaceId);
            }
            final String _tmpPlaceTrigger;
            _tmpPlaceTrigger = _cursor.getString(_cursorIndexOfPlaceTrigger);
            final Integer _tmpNagIntervalMinutes;
            if (_cursor.isNull(_cursorIndexOfNagIntervalMinutes)) {
              _tmpNagIntervalMinutes = null;
            } else {
              _tmpNagIntervalMinutes = _cursor.getInt(_cursorIndexOfNagIntervalMinutes);
            }
            final String _tmpAlertStyle;
            _tmpAlertStyle = _cursor.getString(_cursorIndexOfAlertStyle);
            final String _tmpBackgroundId;
            if (_cursor.isNull(_cursorIndexOfBackgroundId)) {
              _tmpBackgroundId = null;
            } else {
              _tmpBackgroundId = _cursor.getString(_cursorIndexOfBackgroundId);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpSnoozedUntil;
            if (_cursor.isNull(_cursorIndexOfSnoozedUntil)) {
              _tmpSnoozedUntil = null;
            } else {
              _tmpSnoozedUntil = _cursor.getLong(_cursorIndexOfSnoozedUntil);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpSnoozeCount;
            _tmpSnoozeCount = _cursor.getInt(_cursorIndexOfSnoozeCount);
            _item = new Reminder(_tmpId,_tmpTitle,_tmpNotes,_tmpDueAt,_tmpTimezoneId,_tmpRepeatUnit,_tmpRepeatInterval,_tmpRepeatWeekdaysMask,_tmpRepeatMode,_tmpRepeatEndAt,_tmpPriority,_tmpCategoryId,_tmpPlaceId,_tmpPlaceTrigger,_tmpNagIntervalMinutes,_tmpAlertStyle,_tmpBackgroundId,_tmpStatus,_tmpSnoozedUntil,_tmpCreatedAt,_tmpCompletedAt,_tmpSnoozeCount);
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
  public Flow<List<Reminder>> getUpcomingReminders(final long fromTime) {
    final String _sql = "SELECT * FROM reminders WHERE dueAt >= ? AND status != 'COMPLETED' ORDER BY dueAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, fromTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reminders"}, new Callable<List<Reminder>>() {
      @Override
      @NonNull
      public List<Reminder> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfDueAt = CursorUtil.getColumnIndexOrThrow(_cursor, "dueAt");
          final int _cursorIndexOfTimezoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "timezoneId");
          final int _cursorIndexOfRepeatUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatUnit");
          final int _cursorIndexOfRepeatInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatInterval");
          final int _cursorIndexOfRepeatWeekdaysMask = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatWeekdaysMask");
          final int _cursorIndexOfRepeatMode = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatMode");
          final int _cursorIndexOfRepeatEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatEndAt");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfPlaceId = CursorUtil.getColumnIndexOrThrow(_cursor, "placeId");
          final int _cursorIndexOfPlaceTrigger = CursorUtil.getColumnIndexOrThrow(_cursor, "placeTrigger");
          final int _cursorIndexOfNagIntervalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "nagIntervalMinutes");
          final int _cursorIndexOfAlertStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "alertStyle");
          final int _cursorIndexOfBackgroundId = CursorUtil.getColumnIndexOrThrow(_cursor, "backgroundId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfSnoozedUntil = CursorUtil.getColumnIndexOrThrow(_cursor, "snoozedUntil");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfSnoozeCount = CursorUtil.getColumnIndexOrThrow(_cursor, "snoozeCount");
          final List<Reminder> _result = new ArrayList<Reminder>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Reminder _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpDueAt;
            _tmpDueAt = _cursor.getLong(_cursorIndexOfDueAt);
            final String _tmpTimezoneId;
            _tmpTimezoneId = _cursor.getString(_cursorIndexOfTimezoneId);
            final String _tmpRepeatUnit;
            if (_cursor.isNull(_cursorIndexOfRepeatUnit)) {
              _tmpRepeatUnit = null;
            } else {
              _tmpRepeatUnit = _cursor.getString(_cursorIndexOfRepeatUnit);
            }
            final int _tmpRepeatInterval;
            _tmpRepeatInterval = _cursor.getInt(_cursorIndexOfRepeatInterval);
            final int _tmpRepeatWeekdaysMask;
            _tmpRepeatWeekdaysMask = _cursor.getInt(_cursorIndexOfRepeatWeekdaysMask);
            final String _tmpRepeatMode;
            _tmpRepeatMode = _cursor.getString(_cursorIndexOfRepeatMode);
            final Long _tmpRepeatEndAt;
            if (_cursor.isNull(_cursorIndexOfRepeatEndAt)) {
              _tmpRepeatEndAt = null;
            } else {
              _tmpRepeatEndAt = _cursor.getLong(_cursorIndexOfRepeatEndAt);
            }
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpPlaceId;
            if (_cursor.isNull(_cursorIndexOfPlaceId)) {
              _tmpPlaceId = null;
            } else {
              _tmpPlaceId = _cursor.getLong(_cursorIndexOfPlaceId);
            }
            final String _tmpPlaceTrigger;
            _tmpPlaceTrigger = _cursor.getString(_cursorIndexOfPlaceTrigger);
            final Integer _tmpNagIntervalMinutes;
            if (_cursor.isNull(_cursorIndexOfNagIntervalMinutes)) {
              _tmpNagIntervalMinutes = null;
            } else {
              _tmpNagIntervalMinutes = _cursor.getInt(_cursorIndexOfNagIntervalMinutes);
            }
            final String _tmpAlertStyle;
            _tmpAlertStyle = _cursor.getString(_cursorIndexOfAlertStyle);
            final String _tmpBackgroundId;
            if (_cursor.isNull(_cursorIndexOfBackgroundId)) {
              _tmpBackgroundId = null;
            } else {
              _tmpBackgroundId = _cursor.getString(_cursorIndexOfBackgroundId);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpSnoozedUntil;
            if (_cursor.isNull(_cursorIndexOfSnoozedUntil)) {
              _tmpSnoozedUntil = null;
            } else {
              _tmpSnoozedUntil = _cursor.getLong(_cursorIndexOfSnoozedUntil);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpSnoozeCount;
            _tmpSnoozeCount = _cursor.getInt(_cursorIndexOfSnoozeCount);
            _item = new Reminder(_tmpId,_tmpTitle,_tmpNotes,_tmpDueAt,_tmpTimezoneId,_tmpRepeatUnit,_tmpRepeatInterval,_tmpRepeatWeekdaysMask,_tmpRepeatMode,_tmpRepeatEndAt,_tmpPriority,_tmpCategoryId,_tmpPlaceId,_tmpPlaceTrigger,_tmpNagIntervalMinutes,_tmpAlertStyle,_tmpBackgroundId,_tmpStatus,_tmpSnoozedUntil,_tmpCreatedAt,_tmpCompletedAt,_tmpSnoozeCount);
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
  public Object getPendingAlerts(final long now,
      final Continuation<? super List<Reminder>> $completion) {
    final String _sql = "SELECT * FROM reminders WHERE status = 'FIRING' OR (status = 'SNOOZED' AND snoozedUntil <= ?)";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, now);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Reminder>>() {
      @Override
      @NonNull
      public List<Reminder> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final int _cursorIndexOfDueAt = CursorUtil.getColumnIndexOrThrow(_cursor, "dueAt");
          final int _cursorIndexOfTimezoneId = CursorUtil.getColumnIndexOrThrow(_cursor, "timezoneId");
          final int _cursorIndexOfRepeatUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatUnit");
          final int _cursorIndexOfRepeatInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatInterval");
          final int _cursorIndexOfRepeatWeekdaysMask = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatWeekdaysMask");
          final int _cursorIndexOfRepeatMode = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatMode");
          final int _cursorIndexOfRepeatEndAt = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatEndAt");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfPlaceId = CursorUtil.getColumnIndexOrThrow(_cursor, "placeId");
          final int _cursorIndexOfPlaceTrigger = CursorUtil.getColumnIndexOrThrow(_cursor, "placeTrigger");
          final int _cursorIndexOfNagIntervalMinutes = CursorUtil.getColumnIndexOrThrow(_cursor, "nagIntervalMinutes");
          final int _cursorIndexOfAlertStyle = CursorUtil.getColumnIndexOrThrow(_cursor, "alertStyle");
          final int _cursorIndexOfBackgroundId = CursorUtil.getColumnIndexOrThrow(_cursor, "backgroundId");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfSnoozedUntil = CursorUtil.getColumnIndexOrThrow(_cursor, "snoozedUntil");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfSnoozeCount = CursorUtil.getColumnIndexOrThrow(_cursor, "snoozeCount");
          final List<Reminder> _result = new ArrayList<Reminder>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Reminder _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            final long _tmpDueAt;
            _tmpDueAt = _cursor.getLong(_cursorIndexOfDueAt);
            final String _tmpTimezoneId;
            _tmpTimezoneId = _cursor.getString(_cursorIndexOfTimezoneId);
            final String _tmpRepeatUnit;
            if (_cursor.isNull(_cursorIndexOfRepeatUnit)) {
              _tmpRepeatUnit = null;
            } else {
              _tmpRepeatUnit = _cursor.getString(_cursorIndexOfRepeatUnit);
            }
            final int _tmpRepeatInterval;
            _tmpRepeatInterval = _cursor.getInt(_cursorIndexOfRepeatInterval);
            final int _tmpRepeatWeekdaysMask;
            _tmpRepeatWeekdaysMask = _cursor.getInt(_cursorIndexOfRepeatWeekdaysMask);
            final String _tmpRepeatMode;
            _tmpRepeatMode = _cursor.getString(_cursorIndexOfRepeatMode);
            final Long _tmpRepeatEndAt;
            if (_cursor.isNull(_cursorIndexOfRepeatEndAt)) {
              _tmpRepeatEndAt = null;
            } else {
              _tmpRepeatEndAt = _cursor.getLong(_cursorIndexOfRepeatEndAt);
            }
            final String _tmpPriority;
            _tmpPriority = _cursor.getString(_cursorIndexOfPriority);
            final Long _tmpCategoryId;
            if (_cursor.isNull(_cursorIndexOfCategoryId)) {
              _tmpCategoryId = null;
            } else {
              _tmpCategoryId = _cursor.getLong(_cursorIndexOfCategoryId);
            }
            final Long _tmpPlaceId;
            if (_cursor.isNull(_cursorIndexOfPlaceId)) {
              _tmpPlaceId = null;
            } else {
              _tmpPlaceId = _cursor.getLong(_cursorIndexOfPlaceId);
            }
            final String _tmpPlaceTrigger;
            _tmpPlaceTrigger = _cursor.getString(_cursorIndexOfPlaceTrigger);
            final Integer _tmpNagIntervalMinutes;
            if (_cursor.isNull(_cursorIndexOfNagIntervalMinutes)) {
              _tmpNagIntervalMinutes = null;
            } else {
              _tmpNagIntervalMinutes = _cursor.getInt(_cursorIndexOfNagIntervalMinutes);
            }
            final String _tmpAlertStyle;
            _tmpAlertStyle = _cursor.getString(_cursorIndexOfAlertStyle);
            final String _tmpBackgroundId;
            if (_cursor.isNull(_cursorIndexOfBackgroundId)) {
              _tmpBackgroundId = null;
            } else {
              _tmpBackgroundId = _cursor.getString(_cursorIndexOfBackgroundId);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final Long _tmpSnoozedUntil;
            if (_cursor.isNull(_cursorIndexOfSnoozedUntil)) {
              _tmpSnoozedUntil = null;
            } else {
              _tmpSnoozedUntil = _cursor.getLong(_cursorIndexOfSnoozedUntil);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final int _tmpSnoozeCount;
            _tmpSnoozeCount = _cursor.getInt(_cursorIndexOfSnoozeCount);
            _item = new Reminder(_tmpId,_tmpTitle,_tmpNotes,_tmpDueAt,_tmpTimezoneId,_tmpRepeatUnit,_tmpRepeatInterval,_tmpRepeatWeekdaysMask,_tmpRepeatMode,_tmpRepeatEndAt,_tmpPriority,_tmpCategoryId,_tmpPlaceId,_tmpPlaceTrigger,_tmpNagIntervalMinutes,_tmpAlertStyle,_tmpBackgroundId,_tmpStatus,_tmpSnoozedUntil,_tmpCreatedAt,_tmpCompletedAt,_tmpSnoozeCount);
            _result.add(_item);
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
}
