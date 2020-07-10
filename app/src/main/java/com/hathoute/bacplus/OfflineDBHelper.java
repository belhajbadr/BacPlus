package com.hathoute.bacplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class OfflineDBHelper {

    public static int TYPE_LESSON = 0;
    public static int TYPE_EXAM = 1;
    public static int AVAILABLE = 0;
    public static int LAST = 1;

    private static String DATABASE_NAME = "docsmanager";
    private static int DATABASE_VERSION = 1;
    private static String TABLE_AVAILABLE = "docs_available";
    private static String TABLE_LAST = "docs_lastseen";

    private static String COLUMN_TYPE = "TYPE";
    private static String COLUMN_SUBJECT = "SUBJECT";
    private static String COLUMN_ID = "ID";

    private final Context context;
    private final SQLiteOpenHelper _openHelper;

    public OfflineDBHelper(Context context) {
        this.context = context;
        _openHelper = new OfflineDBHelper.SimpleSQLiteOpenHelper(context);
    }

    class SimpleSQLiteOpenHelper extends SQLiteOpenHelper {
        SimpleSQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = "create table " + TABLE_AVAILABLE + " ( " +
                    " _id integer primary key autoincrement," +
                    " TYPE int, " +
                    " SUBJECT int," +
                    " ID int )";
            db.execSQL(sql);

            sql = "create table " + TABLE_LAST + " ( " +
                    " _id integer primary key autoincrement," +
                    " TYPE int, " +
                    " SUBJECT int," +
                    " ID int )";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
        }
    }

    public boolean add(int Table, Object document) throws SQLException {
        SQLiteDatabase db = null;
        try {
            db = _openHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            if (db == null)
                return false;

            db.beginTransaction();

            if (document instanceof Lesson) {
                values.put(COLUMN_TYPE, TYPE_LESSON);
                values.put(COLUMN_SUBJECT, ((Lesson) document).getSubject());
                values.put(COLUMN_ID, ((Lesson) document).getId());
            } else if (document instanceof Exam) {
                values.put(COLUMN_TYPE, TYPE_EXAM);
                values.put(COLUMN_SUBJECT, ((Exam) document).getSubject());
                values.put(COLUMN_ID, ((Exam) document).getId());
            } else
                return false;

            db.insertOrThrow(getTable(Table), null, values);
            db.setTransactionSuccessful();
            return true;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    public boolean delete(int Table, int rowId) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null)
            return false;

        String whereClause = "_id=?";
        String[] whereArgs = new String[]{String.valueOf(rowId)};
        return db.delete(getTable(Table), whereClause, whereArgs) > 0;
    }

    public void clear(int Table) {
        SQLiteDatabase db = _openHelper.getWritableDatabase();
        if (db == null)
            return;

        db.delete(getTable(Table), null, null);
    }

    public Cursor getLastSeen() {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        String sqlQuery = "select * from " + TABLE_LAST  +
                " order by _id desc";
        return db.rawQuery(sqlQuery, new String[] {});
    }

    public Cursor getAvailable() {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        String sqlQuery = "select * from " + TABLE_AVAILABLE  +
                " order by " + COLUMN_SUBJECT + " asc, " +
                COLUMN_TYPE + " asc, " +
                COLUMN_ID + " asc";
        return db.rawQuery(sqlQuery, new String[] {});
    }

    public Cursor getAvailable(int Subject) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        String sqlQuery = "select * from " + TABLE_AVAILABLE  + " where " + COLUMN_SUBJECT + "=? " +
                "order by " + COLUMN_TYPE + " asc, " +
                COLUMN_ID + " asc";
        return db.rawQuery(sqlQuery, new String[] {String.valueOf(Subject)});
    }

    private String getTable(int Table) {
        return Table == AVAILABLE ? TABLE_AVAILABLE : TABLE_LAST;
    }
}
