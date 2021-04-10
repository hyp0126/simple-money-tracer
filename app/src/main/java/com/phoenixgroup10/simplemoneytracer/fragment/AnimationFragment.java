package com.phoenixgroup10.simplemoneytracer.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
 * Use the {@link AnimationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnimationFragment extends Fragment {
    private PieChart pieChart;
    private ActivityDAO activityDAO;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AnimationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnimationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnimationFragment newInstance(String param1, String param2) {
        AnimationFragment fragment = new AnimationFragment();
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
            }

        }

        return v;
    }


}