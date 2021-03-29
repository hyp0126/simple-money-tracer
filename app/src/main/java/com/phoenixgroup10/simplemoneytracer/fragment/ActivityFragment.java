package com.phoenixgroup10.simplemoneytracer.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.phoenixgroup10.simplemoneytracer.MainActivity;
import com.phoenixgroup10.simplemoneytracer.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.phoenixgroup10.simplemoneytracer.SimpleMoneyTracerApplication;
import com.phoenixgroup10.simplemoneytracer.adapter.ActivityListAdapter;
import com.phoenixgroup10.simplemoneytracer.dao.ActivityDAO;
import com.phoenixgroup10.simplemoneytracer.model.ActivityM;
import com.phoenixgroup10.simplemoneytracer.model.Category;


public class ActivityFragment extends Fragment {

    FloatingActionButton mFabAdd;
    // Recycler View variables
    private RecyclerView mRecyclerView;
    private List<ActivityM> mList = new ArrayList<>();
    private ActivityListAdapter mAdapter;

    private ActivityDAO activityDAO;

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

        activityDAO = new ActivityDAO((SimpleMoneyTracerApplication) getActivity().getApplication());

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
        cursor = activityDAO.getActivities("");

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