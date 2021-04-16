package com.phoenixgroup10.simplemoneytracer;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.phoenixgroup10.simplemoneytracer.dao.CategoryDAO;
import com.phoenixgroup10.simplemoneytracer.model.Category;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Common {
    public static final String CATEGORY_TAG = "CategoryDAO";

    public static final int EPOCH_DAY_MS = 86399000;
    public static final int ADD_VISIBLE = 1;
    public static final int ADD_INVISIBLE = 0;

    public static String getFormattedDate(Date date){
        SimpleDateFormat df = new SimpleDateFormat("dd | MMM yyyy, EEEE", Locale.getDefault());
        return df.format(date);
    }

    public static long getCurrentEpoch(){
        final Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        return date.getTime();
    }

    public static long convertDateToEpoch(int y, int m, int d){
        Calendar calendar = Calendar.getInstance();
        calendar.set(y, m, d, 0, 0, 0);
        //calendar.set(year, month, dayOfMonth, 23, 59, 59);
        Date date = calendar.getTime();
        return date.getTime();
    }

    public static int getThemeId(Context context, String from) {

        PreferenceManager.setDefaultValues(context, R.xml.root_preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        if(sharedPref.getBoolean("setDarkOn", false))
            return (from == "category")? R.style.Category_darkTheme : R.style.darkTheme;
        else
            return (sharedPref.getBoolean("setDarkOn", false))
                ? R.style.darkTheme
                : R.style.Theme_SimpleMoneyTracer;
    }
}
