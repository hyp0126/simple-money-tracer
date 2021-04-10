package com.phoenixgroup10.simplemoneytracer.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.data.BarEntry;
import com.google.android.material.snackbar.Snackbar;
import com.phoenixgroup10.simplemoneytracer.R;
import com.phoenixgroup10.simplemoneytracer.SimpleMoneyTracerApplication;
import com.phoenixgroup10.simplemoneytracer.dao.ActivityDAO;
import com.phoenixgroup10.simplemoneytracer.model.ActivityM;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ReportFragment extends Fragment {
    private EditText startDate;
    private EditText endDate;
    private DatePickerDialog mDatePicker;
    private Button btnDaily;
    private Button btnMonthly;
    private Date sDate;
    private Date eDate;
    private Long sDateL;
    private Long eDateL;
    private ArrayList categoryList;
    private ArrayList category;
    private ArrayList dailyList;
    private ArrayList date;
    private SharedPreferences sharedPref;

    private ActivityDAO activityDAO;

    public ReportFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_report, container, false);

        startDate = (EditText)v.findViewById(R.id.editStartDate);
        endDate = (EditText)v.findViewById(R.id.editEndDate);

        btnDaily = (Button)v.findViewById(R.id.btnDaily);
      //  btnMonthly = (Button)v.findViewById(R.id.btnMonthly);

        activityDAO = new ActivityDAO((SimpleMoneyTracerApplication) getActivity().getApplication());
        PreferenceManager.setDefaultValues(getContext(), R.xml.root_preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        startDate.setInputType(InputType.TYPE_NULL);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar cal = Calendar.getInstance();
                int currentDay = cal.get(Calendar.DAY_OF_MONTH);
                int currentMonth = cal.get(Calendar.MONTH);
                int currentYear = cal.get(Calendar.YEAR);

                // Call DatePicker for DOB input
                mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();;
                        startDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);

                        calendar.set(year, month, dayOfMonth);
                        sDate = calendar.getTime();
                    }
                }, currentYear, currentMonth, currentDay);
                mDatePicker.show();
            }
        });

        endDate.setInputType(InputType.TYPE_NULL);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar cal = Calendar.getInstance();
                int currentDay = cal.get(Calendar.DAY_OF_MONTH);
                int currentMonth = cal.get(Calendar.MONTH);
                int currentYear = cal.get(Calendar.YEAR);

                // Call DatePicker for DOB input
                mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();;
                        endDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);

                        calendar.set(year, month, dayOfMonth);
                        eDate = calendar.getTime();
                    }
                }, currentYear, currentMonth, currentDay);
                mDatePicker.show();
            }
        });

        btnDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sDate == null || eDate == null) {
                    Toast.makeText(getContext(), "Date is required", Toast.LENGTH_SHORT).show();
                } else {
                    dataTransfer();
                }
            }


        });
/*
        btnMonthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sDate == null || eDate == null) {
                    Toast.makeText(getContext(), "Date is required", Toast.LENGTH_SHORT).show();
                } else {
                    Cursor cursor = activityDAO.getCategorySum(sDate.getTime(), eDate.getTime());

                    ArrayList categoryList = new ArrayList();
                    ArrayList category = new ArrayList();
                    if(cursor.getCount() > 0){
                        if(cursor.moveToFirst()){
                            int i = 0;
                            do{
                                category.add(cursor.getString(0));
                                categoryList.add(new BarEntry(cursor.getFloat(1), i));
                                i++;
                            }while (cursor.moveToNext());
                        }
                    }

                    cursor = activityDAO.getDailySum(sDate.getTime(), eDate.getTime());

                    ArrayList dailyList = new ArrayList();
                    ArrayList date = new ArrayList();
                    if(cursor.getCount() > 0){
                        if(cursor.moveToFirst()){
                            int i = 0;
                            do{
                                date.add(new Date(cursor.getLong(0)));
                                dailyList.add(new BarEntry(cursor.getFloat(1), i));
                                i++;
                            }while (cursor.moveToNext());
                        }
                    }
                }
            }
        });*/
        return v;
    }

    @Override
    public void onPause() {
        SharedPreferences.Editor ed = sharedPref.edit();

        ed.putLong("sDate", sDate.getTime());
        ed.putLong("eDate", eDate.getTime());
        ed.commit();

        dataTransfer();
        super.onPause();
    }

    @Override
    public void onResume() {
        if (sDate != null && eDate != null){
            sDate = new Date(sharedPref.getLong("sDate",0));
            eDate = new Date(sharedPref.getLong("eDate", 0));
            dataTransfer();
        }

        super.onResume();
    }

    private void dataTransfer() {
        Fragment animationFragment = new AnimationFragment();
        Fragment chartFragment = new ChartFragment();

        Bundle args = new Bundle();
        args.putLong("sDate", sDate.getTime());
        args.putLong("eDate", eDate.getTime());

        animationFragment.setArguments(args);
        FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.fragment, animationFragment);
        trans.commit();

        chartFragment.setArguments(args);
        FragmentTransaction trans1 = getActivity().getSupportFragmentManager().beginTransaction();
        trans1.replace(R.id.fragment2, chartFragment);
        trans1.commit();
    }
}