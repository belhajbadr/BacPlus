package com.hathoute.bacplus;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.FontsContract;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class BacDataDBHelper {

    public static final String DATABASE_NAME = "bacdata";
    public static final int DATABASE_VERSION = 1;

    private final Context context;

    private final SQLiteOpenHelper _openHelper;
    private final String[] subjects;

    public class Columns {
        public static final String Title = "TITLE";
        public static final String Year = "YEAR";
        public static final String Options = "OPTIONS";
        public static final String Link = "LINK";
        public static final String ExamId = "EXAMID";
        public static final String Type = "TYPE";
        public static final String Region = "REGION";
    }

    public BacDataDBHelper(Context context) {
        this.context = context;
        _openHelper = new SimpleSQLiteOpenHelper(context);
        subjects = context.getResources().getStringArray(R.array.subjects_abv);
    }

    class SimpleSQLiteOpenHelper extends SQLiteOpenHelper {
        SimpleSQLiteOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
        }
    }

    public void readifyDB() {
        copyDatabaseFromAssets();
    }

    public Cursor getLessons(int Subject) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        String sqlQuery = "select id from " + formatLessonTable(subjects[Subject]) + " order by id";
        return db.rawQuery(sqlQuery, null);
    }

    public Cursor getLessons(int Subject, int Year) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        String sqlQuery = "select id from " + formatLessonTable(subjects[Subject]) + " where " + Columns.Year + " = ? order by id";
        return db.rawQuery(sqlQuery, new String[] {String.valueOf(Year)});
    }

    public Cursor getLessons(int Subject, int Year, int Option) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        String[] Options = context.getResources().getStringArray(Year == MainActivity.YEAR_FIRST ?
                        R.array.options_firstyear_helper : R.array.options_secondyear_helper);
        String sqlQuery = "select id from " + formatLessonTable(subjects[Subject]) + " where " + Columns.Year + " = ? " +
                "and " + Columns.Options + " like '%$%' order by id";
        return db.rawQuery(sqlQuery.replace("$", Options[Option]), new String[] {String.valueOf(Year)});
    }

    public Lesson getLesson(int Subject, int id) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        //Todo: Configure returned null in other methods.
        if(db == null) {
            return null;
        }
        Cursor cur = db.rawQuery("select * from " + formatLessonTable(subjects[Subject]) +
                " where id = ?", new String[] {String.valueOf(id)});
        ContentValues cv = new ContentValues();

        Lesson lesson;
        cur.moveToFirst();
        try {
            cv.put("id", cur.getInt(0));
            cv.put(Columns.Title, cur.getString(1));
            cv.put(Columns.Year, cur.getInt(2));
            cv.put(Columns.Options, cur.getString(3));
            cv.put(Columns.Link, cur.getString(4));
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        lesson = new Lesson(Subject, cv);
        cur.close();
        db.close();
        return lesson;
    }

    public Cursor getExams(int Subject) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        String sqlQuery = "select id from " + formatExamTable(subjects[Subject])
                + " order by " + Columns.ExamId + " desc, "
                + Columns.Type + " asc, "
                + Columns.Region + " asc";
        return db.rawQuery(sqlQuery, null);
    }

    public Cursor getExams(int Subject, int Year) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        String sqlQuery = "select id from " + formatExamTable(subjects[Subject]) +
                " where " + Columns.Year + " = ? " +
                "order by " + Columns.ExamId + " desc, " +
                Columns.Type + " asc, " +
                Columns.Region + " asc";
        return db.rawQuery(sqlQuery, new String[] {String.valueOf(Year)});
    }

    public Cursor getExams(int Subject, int Year, int Option) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        String[] Options = context.getResources().getStringArray(Year == MainActivity.YEAR_FIRST ?
                R.array.options_firstyear_helper : R.array.options_secondyear_helper);
        String sqlQuery = "select id from " + formatExamTable(subjects[Subject]) + " where " + Columns.Year + " = ? " +
                "and " + Columns.Options + " like '%$%' " +
                "order by " + Columns.ExamId + " desc, " +
                Columns.Type + " asc, " +
                Columns.Region + " asc";
        return db.rawQuery(sqlQuery.replace("$", Options[Option]), new String[] {String.valueOf(Year)});
    }

    public Exam getExam(int Subject, int id) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        //Todo: Configure returned null in other methods.
        if(db == null) {
            return null;
        }
        Cursor cur = db.rawQuery("select * from " + formatExamTable(subjects[Subject]) +
                " where id = ?", new String[] {String.valueOf(id)});
        ContentValues cv = new ContentValues();

        Exam exam;
        cur.moveToFirst();
        try {
            cv.put("id", cur.getInt(0));
            cv.put(Columns.Year, cur.getInt(1));
            cv.put(Columns.Options, cur.getString(2));
            cv.put(Columns.Type, cur.getInt(3));
            cv.put(Columns.ExamId, cur.getInt(4));
            cv.put(Columns.Region, cur.getInt(5));
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        exam = new Exam(Subject, cv);
        cur.close();
        db.close();
        return exam;
    }

    public Cursor getVideos(int Subject, int Year) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        String sqlQuery = "select * from " + formatVideoTable(subjects[Subject]) +
                " where " + Columns.Year + " = ? " +
                "order by id desc";
        return db.rawQuery(sqlQuery, new String[] {String.valueOf(Year)});
    }

    public Cursor getVideos(int Subject, int Year, int Option) {
        SQLiteDatabase db = _openHelper.getReadableDatabase();
        if (db == null) {
            return null;
        }
        String[] Options = context.getResources().getStringArray(Year == MainActivity.YEAR_FIRST ?
                R.array.options_firstyear_helper : R.array.options_secondyear_helper);
        String sqlQuery = "select * from " + formatVideoTable(subjects[Subject]) + " where " + Columns.Year + " = ? " +
                "and " + Columns.Options + " like '%$%' " +
                "order by id desc";
        return db.rawQuery(sqlQuery.replace("$", Options[Option]), new String[] {String.valueOf(Year)});
    }

    public void copyDatabaseFromAssets() {
        InputStream myInput = null;
        OutputStream myOutput = null;
        try {
            // Open db packaged as asset as the input stream
            myInput = context.getAssets().open(DATABASE_NAME + ".db");

            // Open the db in the application package context:
            myOutput = new FileOutputStream(context.getDatabasePath(DATABASE_NAME));
            File file = new File(context.getDatabasePath(DATABASE_NAME), DATABASE_NAME + ".db");
            if(file.exists()) {
                file.delete();
            }

            // Transfer db file contents:
            byte[] buffer = new byte[1024];
            int length;
            int i = 0;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
                i += length;

            }

            myOutput.flush();

            // Set the version of the copied database to the current
            // version:
            SQLiteDatabase copiedDb = context.openOrCreateDatabase(
                    DATABASE_NAME, 0, null);
            copiedDb.execSQL("PRAGMA user_version = " + DATABASE_VERSION);
            copiedDb.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Close the streams
            try {
                if (myOutput != null) {
                    myOutput.close();
                }
                if (myInput != null) {
                    myInput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String formatLessonTable(String subject) {
        return "lessons_" + subject;
    }

    private static String formatExamTable(String subject) {
        return "exams_" + subject;
    }

    private static String formatVideoTable(String subject) {
        return "videos_" + subject;
    }
}
