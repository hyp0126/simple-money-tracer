package com.phoenixgroup10.simplemoneytracer.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.phoenixgroup10.simplemoneytracer.SimpleMoneyTracerApplication;
import com.phoenixgroup10.simplemoneytracer.model.ActivityM;

public class ActivityDAO {
    public static final String ACTIVITY_TABLE_NAME = "activity";
    public static final String ACTIVITY_COL1 = "id";
    public static final String ACTIVITY_COL2 = "amount";
    public static final String ACTIVITY_COL3 = "category_id";
    public static final String ACTIVITY_COL4 = "date";

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
}

