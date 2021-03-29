package com.phoenixgroup10.simplemoneytracer.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.phoenixgroup10.simplemoneytracer.SimpleMoneyTracerApplication;
import com.phoenixgroup10.simplemoneytracer.model.Category;

public class CategoryDAO {

    public static final String CATEGORY_TABLE_NAME = "category";
    public static final String CATEGORY_COL1 = "id";
    public static final String CATEGORY_COL2 = "name";

    // Query for creating table
    public static final String CREATE_CATEGORY_TABLE = "CREATE TABLE " + CATEGORY_TABLE_NAME + " ( "
            + CATEGORY_COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CATEGORY_COL2 + " TEXT);";

    private SQLiteOpenHelper helper;

    public CategoryDAO(SimpleMoneyTracerApplication application) {
        helper = application.getSQLiteOpenHelper();
    }

    public boolean insertCategory(Category objCategory) {
        SQLiteDatabase db = helper.getWritableDatabase();

        // Set category data for adding
        ContentValues cv = new ContentValues();
        cv.put(CATEGORY_COL2, objCategory.getName());

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

        // Set category data for update
        ContentValues cv = new ContentValues();
        cv.put(CATEGORY_COL2, objCategory.getName());

        // Update category data
        long result = db.update(CATEGORY_TABLE_NAME, cv, "id = " +  objCategory.getId(), null);
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

        // Delete category data corresponding to category Id
        long result = db.delete(CATEGORY_TABLE_NAME, "id = " +  id, null);
        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public Cursor getCategories(String whereString)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor;

        // If user select all data or not
        if (whereString.isEmpty())
        {
            cursor = db.rawQuery("SELECT * FROM " + CATEGORY_TABLE_NAME, null);
        }
        else
        {
            cursor = db.rawQuery("SELECT * FROM " + CATEGORY_TABLE_NAME + " " + whereString, null);
        }

        // If any data exist, go to first row
        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
        }
        return cursor;
    }
}
