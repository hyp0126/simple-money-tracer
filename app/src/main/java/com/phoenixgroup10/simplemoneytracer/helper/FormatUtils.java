package com.phoenixgroup10.simplemoneytracer.helper;

import android.text.InputFilter;
import android.text.Spanned;

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

    public static String getCurrencyString(double currency) {
        return "$" + String.valueOf(currency);
    }
}
