package com.phoenixgroup10.simplemoneytracer.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.BarEntry;
import com.phoenixgroup10.simplemoneytracer.R;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.phoenixgroup10.simplemoneytracer.SimpleMoneyTracerApplication;
import com.phoenixgroup10.simplemoneytracer.dao.ActivityDAO;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AnimationFragment extends Fragment {
    private PieChart pieChart;
    private ActivityDAO activityDAO;
    private SharedPreferences sharedPref;

    public AnimationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        PreferenceManager.setDefaultValues(getContext(), R.xml.root_preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        View v = inflater.inflate(R.layout.fragment_animation, container, false);

        pieChart = (PieChart) v.findViewById(R.id.piechart);
        activityDAO = new ActivityDAO((SimpleMoneyTracerApplication) getActivity().getApplication());

        ArrayList categoryList = new ArrayList();
        ArrayList category = new ArrayList();

        Long sDate = null;
        Long eDate = null;
        if (getArguments() != null){
            sDate = getArguments().getLong("sDate");
            eDate = getArguments().getLong("eDate");

            Cursor cursor = activityDAO.getCategorySum(sDate, eDate);

            if(cursor.getCount() > 0){
                if(cursor.moveToFirst()){
                    int i = 0;
                    do{
                        category.add(cursor.getString(0));
                        categoryList.add(new BarEntry(cursor.getFloat(1), i));
                        i++;
                    }while (cursor.moveToNext());
                }
                PieDataSet pieDataSet = new PieDataSet(categoryList, "Category");
                pieDataSet.setValueTextSize(16f);
                PieData pieData = new PieData(category, pieDataSet);
                pieChart.setData(pieData);
                pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                pieChart.animateXY(5000,5000);
                pieChart.setDescription("");
            }

        }

        return v;
    }


}