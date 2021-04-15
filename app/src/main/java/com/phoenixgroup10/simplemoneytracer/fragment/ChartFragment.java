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
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartFragment extends Fragment {
    private BarChart chart;
    private ActivityDAO activityDAO;
    private SharedPreferences sharedPref;
    private boolean darkState;
    private int chartColor;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChartFragment newInstance(String param1, String param2) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        PreferenceManager.setDefaultValues(getContext(), R.xml.root_preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        darkState = sharedPref.getBoolean("setDarkOn", false);
        if(darkState) {
            getActivity().setTheme((R.style.darkTheme));
            chartColor = Color.WHITE;
        }
        else {
            getActivity().setTheme(R.style.Theme_SimpleMoneyTracer);
            chartColor = Color.BLACK;
        }

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
                chart.getAxisLeft().setTextColor(chartColor);
                chart.getAxisRight().setTextColor(chartColor);
                chart.getXAxis().setTextColor(chartColor);
                chart.getLegend().setTextColor(chartColor);
                chart.setDescriptionColor(chartColor);

                chart.setData(data);
            }
        }

        return v;
    }
}