package com.phoenixgroup10.simplemoneytracer.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.phoenixgroup10.simplemoneytracer.SimpleMoneyTracerApplication;
import com.phoenixgroup10.simplemoneytracer.model.ActivityM;

import java.util.Calendar;
import java.util.Date;

public class ActivityDAO {
    public static final String ACTIVITY_TABLE_NAME = "activity";
    public static final String ACTIVITY_COL1 = "id";
    public static final String ACTIVITY_COL2 = "amount";
    public static final String ACTIVITY_COL3 = "category_id";
    public static final String ACTIVITY_COL4 = "date";


    public static final int INCOME_ONLY = 2;
    public static final int EXPENSE_ONLY = 1;
    public static final int ALL = 0;

    public static final String CREATE_ACTIVITY_TABLE = "CREATE TABLE " + ACTIVITY_TABLE_NAME + " ( "
            + ACTIVITY_COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "   + ACTIVITY_COL2 + " REAL, "
            + ACTIVITY_COL3 + " INTEGER, " + ACTIVITY_COL4 + " INTEGER);";

    private SQLiteOpenHelper helper;

    public ActivityDAO(SimpleMoneyTracerApplication application) {
        helper = application.getSQLiteOpenHelper();
    }

    public boolean insertActivity(ActivityM objActivity) {
        SQLiteDatabase db = helper.getWritableDatabase();

        // Set activity data for adding
        ContentValues cv = new ContentValues();
        cv.put(ACTIVITY_COL2, objActivity.getAmount());
        cv.put(ACTIVITY_COL3, objActivity.getCategoryId());
        cv.put(ACTIVITY_COL4, objActivity.getDateEpoch());

        // Save activity data
        long result = db.insert(ACTIVITY_TABLE_NAME, null, cv);
        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean updateActivity(ActivityM objActivity)
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        // Set activity data for update
        ContentValues cv = new ContentValues();
        cv.put(ACTIVITY_COL2, objActivity.getAmount());
        cv.put(ACTIVITY_COL3, objActivity.getCategoryId());
        cv.put(ACTIVITY_COL4, objActivity.getDateEpoch());

        // Update activity data
        long result = db.update(ACTIVITY_TABLE_NAME, cv, "id = " +  objActivity.getId(), null);
        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean deleteActivity(int id)
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        // Delete activity data corresponding to activity Id
        long result = db.delete(ACTIVITY_TABLE_NAME, "id = " +  id, null);
        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public Cursor getActivities(String whereString)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;

        // If user select all data or not
        if (whereString.isEmpty())
        {
            cursor = db.rawQuery("SELECT * FROM " + ACTIVITY_TABLE_NAME, null);
        }
        else
        {
            cursor = db.rawQuery("SELECT * FROM " + ACTIVITY_TABLE_NAME + " " + whereString, null);
        }

        // If any data exist, go to first row
        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getActivitiesWithDates(Date startDate, Date endDate)
    {
        //only use date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        calendar.set(year, month, dayOfMonth, 00, 00, 00);
        startDate = calendar.getTime();

        calendar.setTime(endDate);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        calendar.set(year, month, dayOfMonth, 23, 59, 59);
        endDate = calendar.getTime();

        long startEpochDate = startDate.getTime();
        long endEpochDate = endDate.getTime();
        String whereString = "WHERE " + ACTIVITY_COL4 + " >= " + startEpochDate + " AND "
                + ACTIVITY_COL4 + " <= " + endEpochDate;

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;

        cursor = db.rawQuery("SELECT * FROM " + ACTIVITY_TABLE_NAME + " " + whereString, null);

        // If any data exist, go to first row
        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Double getSumWithDates(Date startDate, Date endDate, int sumType)
    {
        //only use date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        calendar.set(year, month, dayOfMonth, 00, 00, 00);
        startDate = calendar.getTime();

        calendar.setTime(endDate);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        calendar.set(year, month, dayOfMonth, 23, 59, 59);
        endDate = calendar.getTime();

        long startEpochDate = startDate.getTime();
        long endEpochDate = endDate.getTime();

        String whereString;
        if (sumType == INCOME_ONLY) { //income
            whereString = "WHERE " + ACTIVITY_COL4 + " >= " + startEpochDate + " AND "
                    + ACTIVITY_COL4 + " <= " + endEpochDate + " AND "
                    + ACTIVITY_COL2 + " >= 0"; //only income
        }
        else if (sumType == EXPENSE_ONLY) {  // expense
            whereString = "WHERE " + ACTIVITY_COL4 + " >= " + startEpochDate + " AND "
                    + ACTIVITY_COL4 + " <= " + endEpochDate + " AND "
                    + ACTIVITY_COL2 + " < 0"; //only expense
        }
        else {  // all
            whereString = "WHERE " + ACTIVITY_COL4 + " >= " + startEpochDate + " AND "
                    + ACTIVITY_COL4 + " <= " + endEpochDate;
        }

        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;

        cursor = db.rawQuery("SELECT SUM(" + ACTIVITY_COL2 + ") FROM " + ACTIVITY_TABLE_NAME + " " + whereString, null);

        // If any data exist, go to first row
        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            return cursor.getDouble(0);
        }
        return 0.0;
    }

    public Cursor getCategorySum(long sdate, long edate)
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor;

        String myQuery = "SELECT a.name as name, ABS(SUM(b.amount)) FROM category a INNER JOIN activity b ON a.id = b.category_id WHERE DATE >= " + sdate + " AND DATE <= " + edate + " GROUP BY a.name;";
        cursor = db.rawQuery( myQuery, null);

        // If any data exist, go to first row
        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public Cursor getDailySum(long sdate, long edate)
    {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor;

        String myQuery = "SELECT date as date, SUM(amount) FROM activity WHERE DATE >= " + sdate + " AND DATE <= " + edate + " GROUP BY date;";
        cursor = db.rawQuery( myQuery, null);

        // If any data exist, go to first row
        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
        }
        return cursor;
    }
}

