package com.phoenixgroup10.simplemoneytracer.fragment;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.EventLogTags;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.phoenixgroup10.simplemoneytracer.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.phoenixgroup10.simplemoneytracer.SimpleMoneyTracerApplication;
import com.phoenixgroup10.simplemoneytracer.dao.ActivityDAO;
import com.phoenixgroup10.simplemoneytracer.helper.FormatUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ChartFragment extends Fragment {
    private BarChart chart;
    private ActivityDAO activityDAO;
    private SharedPreferences sharedPref;

    public ChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        PreferenceManager.setDefaultValues(getContext(), R.xml.root_preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        View v = inflater.inflate(R.layout.fragment_chart, container, false);

        chart = (BarChart) v.findViewById(R.id.barchart);
        activityDAO = new ActivityDAO((SimpleMoneyTracerApplication) getActivity().getApplication());

        ArrayList dailyList = new ArrayList();
        ArrayList date = new ArrayList();

        Long sDate = null;
        Long eDate = null;

        if (getArguments() != null){
            sDate = getArguments().getLong("sDate");
            eDate = getArguments().getLong("eDate");

            dailyList = new ArrayList();
            date = new ArrayList();

            Cursor cursor = activityDAO.getDailySum(sDate, eDate);

            dailyList = new ArrayList();
            date = new ArrayList();
            if(cursor.getCount() > 0){
                if(cursor.moveToFirst()){
                    int i = 0;
                    do{
                        Date useDate = new Date(cursor.getLong(0));
                        date.add(FormatUtils.getDateString(useDate));
                       // date.add(new Date(cursor.getLong(0)).toString());
                        dailyList.add(new BarEntry(cursor.getFloat(1), i));
                        i++;
                    }while (cursor.moveToNext());
                }
                BarDataSet bardataset = new BarDataSet(dailyList, "Date");

                chart.animateY(5000);
                BarData data = new BarData(date, bardataset);
                bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                chart.setData(data);
                chart.setDescription("");
            }
        }

        return v;
    }
}