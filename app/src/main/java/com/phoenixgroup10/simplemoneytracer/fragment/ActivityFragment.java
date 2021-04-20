package com.phoenixgroup10.simplemoneytracer.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.phoenixgroup10.simplemoneytracer.MainActivity;
import com.phoenixgroup10.simplemoneytracer.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.phoenixgroup10.simplemoneytracer.SimpleMoneyTracerApplication;
import com.phoenixgroup10.simplemoneytracer.adapter.ActivityListAdapter;
import com.phoenixgroup10.simplemoneytracer.dao.ActivityDAO;
import com.phoenixgroup10.simplemoneytracer.helper.FormatUtils;
import com.phoenixgroup10.simplemoneytracer.model.ActivityM;
import com.phoenixgroup10.simplemoneytracer.model.Category;


public class ActivityFragment extends Fragment {

    FloatingActionButton mFabAdd;
    private EditText mEdtStartDate;
    private EditText mEdtEndDate;

    // Recycler View variables
    private RecyclerView mRecyclerView;
    private List<ActivityM> mList = new ArrayList<>();
    private ActivityListAdapter mAdapter;

    private DatePickerDialog mDatePicker;

    private ActivityDAO activityDAO;

    // Member variables
    private Date mStartDate;
    private Date mEndDate;

    public ActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_activity, container, false);

        mRecyclerView = (RecyclerView)v.findViewById(R.id.recyclerViewActivities);
        mFabAdd = v.findViewById(R.id.fabAdd);
        mEdtStartDate = (EditText)v.findViewById(R.id.edtStartDate);
        mEdtEndDate = (EditText)v.findViewById(R.id.edtEndDate);

        activityDAO = new ActivityDAO((SimpleMoneyTracerApplication) getActivity().getApplication());

        Calendar cal = Calendar.getInstance();
        mEndDate = cal.getTime();
        mEdtEndDate.setText(FormatUtils.getDateString(mEndDate));
        cal.add(Calendar.DAY_OF_MONTH, -30);
        mStartDate = cal.getTime();
        mEdtStartDate.setText(FormatUtils.getDateString(mStartDate));

        // Get activities list
        getActivities();

        // binding an adapter for Recycler view
        bindAdapter();

        // Floating Button (+): Go to Activity for adding new activity data
        mFabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment frag = new AddActivityFragment();
                FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                trans.replace(R.id.frame, frag);
                trans.commit();
            }
        });

        // If click Date text input window, call DatePicker
        mEdtStartDate.setInputType(InputType.TYPE_NULL);
        mEdtStartDate.setOnClickListener(new View.OnClickListener() {
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
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        mStartDate = calendar.getTime();
                        mEdtStartDate.setText(FormatUtils.getDateString(mStartDate));
                        getActivities();
                        mAdapter.notifyDataSetChanged();
                    }
                }, currentYear, currentMonth, currentDay);
                mDatePicker.show();
            }
        });

        // If click Date text input window, call DatePicker
        mEdtEndDate.setInputType(InputType.TYPE_NULL);
        mEdtEndDate.setOnClickListener(new View.OnClickListener() {
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
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        mEndDate = calendar.getTime();
                        mEdtEndDate.setText(FormatUtils.getDateString(mEndDate));
                        getActivities();
                        mAdapter.notifyDataSetChanged();
                    }
                }, currentYear, currentMonth, currentDay);
                mDatePicker.show();
            }
        });

        return v;
    }

    /**
     * Search corresponding to user inputs, and then Add activity list to recycler view data
     */
    private void getActivities()
    {
        final SimpleMoneyTracerApplication application;
        Cursor cursor;

        // Get cursor for getting a list
        //cursor = activityDAO.getActivities("");
        cursor = activityDAO.getActivitiesWithDates(mStartDate, mEndDate);

        ActivityM activityObj = new ActivityM();
        mList.clear();

        if (cursor.getCount() == 0)
        {
            Toast.makeText(getActivity(), "No Record", Toast.LENGTH_LONG).show();
        }
        else
        {
            if (cursor.moveToFirst())
            {
                // Set found activity to RecyclerView
                do {
                    activityObj = new ActivityM();
                    activityObj.setId(cursor.getInt(0));
                    activityObj.setAmount(cursor.getDouble(1));
                    activityObj.setCategoryId(cursor.getInt(2));
                    activityObj.setDate(new Date(cursor.getLong(3)));

                    mList.add(activityObj);
                } while (cursor.moveToNext());
            }
        }

        Collections.sort(mList, new Comparator<ActivityM>(){
            public int compare(ActivityM obj1, ActivityM obj2) {
                // Descending order
                return (int)(obj2.getDateEpoch()/60000 - obj1.getDateEpoch()/60000);
            }
        });
    }

    /**
     * This method for binding an adapter for RecyclerView
     */
    private void bindAdapter()
    {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new ActivityListAdapter(mList, getActivity());

        // Set Click event to Recycler view
        mAdapter.setOnItemClickListener(new ActivityListAdapter.onClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent;

                if (v.getId() == R.id.btnEditActivity) {
                    // Go to Activity for adding a new activity
                    Fragment frag = new AddActivityFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("ACTIVITY_ID", mAdapter.getActivityId(position));
                    frag.setArguments(bundle);
                    FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                    trans.replace(R.id.frame, frag);
                    trans.commit();
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * OnStop State: Remove Recycler View Adpater for preventing memory leak
     */
    @Override
    public void onStop()
    {
        // Prevent Memory Leak
        mRecyclerView.setAdapter(null);
        super.onStop();
    }

    /**
     * OnRestart State: Re-Binding Recycler View Adapter
     */
    @Override
    public void onResume() {
        // Re-Bind Recycler View Adapter because it is removed onStop()
        bindAdapter();
        super.onResume();
    }
}