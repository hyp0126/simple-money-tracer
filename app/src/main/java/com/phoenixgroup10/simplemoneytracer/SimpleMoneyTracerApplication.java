package com.phoenixgroup10.simplemoneytracer;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.phoenixgroup10.simplemoneytracer.dao.ActivityDAO;
import com.phoenixgroup10.simplemoneytracer.dao.CategoryDAO;
import com.phoenixgroup10.simplemoneytracer.model.ActivityM;
import com.phoenixgroup10.simplemoneytracer.model.Category;

public class SimpleMoneyTracerApplication extends Application {
    // Constants for Database name and version
    private static final String DB_NAME = "db_simple_money_tracer1";
    private static final int DB_VERSION = 1;

    // For manipulating SQLite
    private SQLiteOpenHelper helper;

    @Override
    public void onCreate() {
        helper = new SQLiteOpenHelper(this, DB_NAME, null, DB_VERSION) {

            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL(ActivityDAO.CREATE_ACTIVITY_TABLE);
                db.execSQL(CategoryDAO.CREATE_CATEGORY_TABLE);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + CategoryDAO.CATEGORY_TABLE_NAME);
                onCreate(db);
            }
        };
        super.onCreate();
    }

    public SQLiteOpenHelper getSQLiteOpenHelper() {
        return helper;
    }
}
