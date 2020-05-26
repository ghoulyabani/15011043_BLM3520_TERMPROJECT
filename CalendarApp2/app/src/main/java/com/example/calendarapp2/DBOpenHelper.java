package com.example.calendarapp2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DBOpenHelper extends SQLiteOpenHelper {

    private static final String CREATE_EVENTS_TABLE = "create table " + DBStruct.EVENT_TABLE_NAME+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DBStruct.EVENT + " TEXT, " + DBStruct.DETAIL + " TEXT, " + DBStruct.STARTTIME + " TEXT, " + DBStruct.FINISHTIME  + " TEXT, " +
            DBStruct.REMINDTIMER + " TEXT, " + DBStruct.REPEAT + " TEXT, " + DBStruct.DATE + " TEXT, " + DBStruct.MONTH + " TEXT, " +
            DBStruct.YEAR + " TEXT, " + DBStruct.ADRESS + " TEXT)";
    private static final String DROP_EVENTS_TABLE = "DROP TABLE IF EXISTS " + DBStruct.EVENT_TABLE_NAME;

    public DBOpenHelper(@Nullable Context context) {
        super(context, DBStruct.DB_NAME, null, DBStruct.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_EVENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_EVENTS_TABLE);
        onCreate(db);
    }

    public void SaveEvent (String event, String detail, String startTime, String finishTime, String remindTimer,
                           String repeat, String date, String month, String year, String address, SQLiteDatabase dataBase){
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBStruct.EVENT, event);
        contentValues.put(DBStruct.DETAIL, detail);
        contentValues.put(DBStruct.STARTTIME, startTime);
        contentValues.put(DBStruct.FINISHTIME, finishTime);
        contentValues.put(DBStruct.REMINDTIMER, remindTimer);
        contentValues.put(DBStruct.REPEAT, repeat);
        contentValues.put(DBStruct.DATE, date);
        contentValues.put(DBStruct.MONTH, month);
        contentValues.put(DBStruct.YEAR, year);
        contentValues.put(DBStruct.ADRESS, address);
        dataBase.insert(DBStruct.EVENT_TABLE_NAME, null, contentValues);
    }

    public Cursor ReadEvents(String date, SQLiteDatabase dataBase){
        String [] projections = {DBStruct.EVENT, DBStruct.DETAIL, DBStruct.STARTTIME, DBStruct.FINISHTIME,
            DBStruct.REMINDTIMER, DBStruct.REPEAT, DBStruct.DATE, DBStruct.MONTH, DBStruct.YEAR, DBStruct.ADRESS};
        String selection = DBStruct.DATE + "=?";
        String [] selectionArgs = {date};

        return dataBase.query(DBStruct.EVENT_TABLE_NAME, projections, selection, selectionArgs, null, null, null);
    }

    public Cursor ReadEventsPerMonth(String month, String year, SQLiteDatabase dataBase){
        String [] projections = {DBStruct.EVENT, DBStruct.DETAIL, DBStruct.STARTTIME, DBStruct.FINISHTIME,
                DBStruct.REMINDTIMER, DBStruct.REPEAT, DBStruct.DATE, DBStruct.MONTH, DBStruct.YEAR, DBStruct.ADRESS};
        String selection = DBStruct.MONTH + "=? and " + DBStruct.YEAR + "=?";
        String [] selectionArgs = {month, year};

        return dataBase.query(DBStruct.EVENT_TABLE_NAME, projections, selection, selectionArgs, null, null, null);
    }

    public void DeleteEvent(String event, String date, String startTime, String endTime, SQLiteDatabase database){
        String selection = DBStruct.EVENT + "=? and " + DBStruct.DATE + "=? and " + DBStruct.STARTTIME + "=? and " +
                DBStruct.FINISHTIME + "=?";
        String[] selectionArgs = {event, date, startTime, endTime};
        database.delete(DBStruct.EVENT_TABLE_NAME, selection, selectionArgs);
    }
}
