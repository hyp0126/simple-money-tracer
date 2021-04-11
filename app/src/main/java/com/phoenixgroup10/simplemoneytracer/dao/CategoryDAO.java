package com.phoenixgroup10.simplemoneytracer.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.phoenixgroup10.simplemoneytracer.Common;
import com.phoenixgroup10.simplemoneytracer.SimpleMoneyTracerApplication;
import com.phoenixgroup10.simplemoneytracer.model.Category;

public class CategoryDAO {

    public static final String CATEGORY_TABLE_NAME = "category";
    public static final String CATEGORY_ID = "id";
    public static final String CATEGORY_NAME = "name";

    // Query for creating table
    public static final String CREATE_CATEGORY_TABLE = "CREATE TABLE IF NOT EXISTS " + CATEGORY_TABLE_NAME + " ( "
            + CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CATEGORY_NAME + " TEXT);";

    private SQLiteOpenHelper helper;

    public CategoryDAO(SimpleMoneyTracerApplication application) {
        helper = application.getSQLiteOpenHelper();
    }

    public boolean insertCategory(Category objCategory) {
        SQLiteDatabase db = helper.getWritableDatabase();

        // Set category data for adding
        ContentValues cv = new ContentValues();
        cv.put(CATEGORY_NAME, objCategory.getName());

        // Save category data
        long result = db.insert(CATEGORY_TABLE_NAME, null, cv);
        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean updateCategory(Category objCategory)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        String whereClause = CATEGORY_ID + "=?";
        String whereArgs[] = {String.valueOf(objCategory.getId())};
        // Set category data for update
        ContentValues cv = new ContentValues();
        cv.put(CATEGORY_NAME, objCategory.getName());

        // Update category data
        long result = db.update(CATEGORY_TABLE_NAME, cv, whereClause, whereArgs);
        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean deleteCategory(int id)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        String whereClause = CATEGORY_ID + "=?";
        String whereArgs[] = {String.valueOf(id)};

        // Delete category data corresponding to category Id
        int result = db.delete(CATEGORY_TABLE_NAME, whereClause, whereArgs);
        if (result > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public Cursor getCategories(String whereString)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CATEGORY_TABLE_NAME + " " + whereString, null);
        return cursor;
    }
}
