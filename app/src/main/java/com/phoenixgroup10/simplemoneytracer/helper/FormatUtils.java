package com.phoenixgroup10.simplemoneytracer.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.InputFilter;
import android.text.Spanned;

import androidx.preference.PreferenceManager;

import com.phoenixgroup10.simplemoneytracer.R;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatUtils {

    public static String getDateString(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);
        int currentMonth = cal.get(Calendar.MONTH);
        int currentYear = cal.get(Calendar.YEAR);
        return "" + currentYear + "-" + (currentMonth + 1) + "-" + currentDay;
    }

    public static String getCurrencyString(Context context, double currency) {
        PreferenceManager.setDefaultValues(context, R.xml.root_preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String currencyUnit = sharedPref.getString("currency", "$");

        return currencyUnit + String.format("%.2f", currency);
    }
}
