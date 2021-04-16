package com.phoenixgroup10.simplemoneytracer.fragment;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.text.InputFilter;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;


import com.google.android.material.snackbar.Snackbar;
import com.phoenixgroup10.simplemoneytracer.R;
import com.phoenixgroup10.simplemoneytracer.SimpleMoneyTracerApplication;
import com.phoenixgroup10.simplemoneytracer.dao.ActivityDAO;
import com.phoenixgroup10.simplemoneytracer.dao.CategoryDAO;
import com.phoenixgroup10.simplemoneytracer.helper.CurrencyFormatInputFilter;
import com.phoenixgroup10.simplemoneytracer.helper.FormatUtils;
import com.phoenixgroup10.simplemoneytracer.model.ActivityM;
import com.phoenixgroup10.simplemoneytracer.model.Category;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;


public class AddActivityFragment extends Fragment {

    private List<String> categoryList = new ArrayList<String>();
    private List<Integer> categoryListId = new ArrayList<Integer>();

    private FrameLayout fLayout;
    // Declare UI instance variables
    private EditText mEdtAmount;
    private DatePickerDialog mDatePicker;
    private EditText mEdtDate;
    private Spinner mSpnCategory;
    private Button mBtnSave;
    private Button mBtnDelete;
    private Button mBtnCancel;
    private CheckBox mChkIncome;

    // Member variables
    private int mActivityId;
    private int mCategoryId;
    private Date mDate;

    private ActivityDAO activityDAO;
    private CategoryDAO categoryDAO;

    private SharedPreferences sharedPref;

    public AddActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_activity, container, false);

        fLayout = (FrameLayout) v.findViewById(R.id.addActivityLayout);
        // Set UI instance variables
        mEdtAmount = (EditText)v.findViewById(R.id.edtAmount);
        mEdtAmount.setFilters(new InputFilter[] {new CurrencyFormatInputFilter()});

        mEdtDate = (EditText)v.findViewById(R.id.edtDate);

        mChkIncome = (CheckBox)v.findViewById(R.id.chkIncome);

        mBtnSave = (Button)v.findViewById(R.id.btnSave);
        mBtnDelete = (Button)v.findViewById(R.id.btnDelete);
        mBtnCancel = (Button)v.findViewById(R.id.btnCancel);

        activityDAO = new ActivityDAO((SimpleMoneyTracerApplication) getActivity().getApplication());
        categoryDAO = new CategoryDAO((SimpleMoneyTracerApplication) getActivity().getApplication());

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        getCategoryList();

        // Set Spinner for Province input
        mSpnCategory = (Spinner) v.findViewById(R.id.spnCategory);
        ArrayAdapter<String> adpProvince = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, categoryList);
        adpProvince.setDropDownViewResource(R.layout.spinner_item);
        mSpnCategory.setAdapter(adpProvince);

        // If click spinner item, Keep it to member variable
        mSpnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCategoryId = categoryListId.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Snackbar.make(fLayout,"Please, select category", Snackbar.LENGTH_SHORT).show();
            }
        });

        // If click Date text input window, call DatePicker
        mEdtDate.setInputType(InputType.TYPE_NULL);
        Calendar cal = Calendar.getInstance();
        mEdtDate.setText(FormatUtils.getDateString(cal.getTime()));
        mEdtDate.setOnClickListener(new View.OnClickListener() {
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
                        mDate = calendar.getTime();
                        mEdtDate.setText(FormatUtils.getDateString(mDate));
                    }
                }, currentYear, currentMonth, currentDay);
                mDatePicker.show();
            }
        });



        // Save button: Save current activity data, and go back to activities list (previous activity)
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                double amount = Double.parseDouble(mEdtAmount.getText().toString());
                if (!mChkIncome.isChecked()) {
                    amount = -amount;
                }
                // Save activity data
                ActivityM activity = new ActivityM(mActivityId, amount,
                        mCategoryId,
                        mDate);

                boolean insertStat;
                // If new activity data, insert it
                // If not, update it
                if (mActivityId == 0)
                {
                    insertStat = activityDAO.insertActivity(activity);
                }
                else
                {
                    insertStat = activityDAO.updateActivity(activity);
                }

                // Toast message for user (success or not)
                if (insertStat == true)
                {
                    Snackbar.make(fLayout,"Activity added/updated successfully", Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    Snackbar.make(fLayout,"Activity not added/updated", Snackbar.LENGTH_SHORT).show();
                }

                closeKeyboard();

                // Check the monthly target expense
                // Get Current Date
                final Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, 1);
                Date sDate = cal.getTime();
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                Date eDate = cal.getTime();
                double monthlyAmount = activityDAO.getSumWithDates(sDate, eDate);
                double targetExpense = Double.MAX_VALUE;
                try {
                    targetExpense = Double.parseDouble(sharedPref.getString("targetExpense", "0"));
                }
                catch (Exception ex) {
                    //parse error
                }

                if (monthlyAmount > targetExpense) {
                    Snackbar.make(fLayout,"Over the monthly target expense " + targetExpense, Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            })
                            .show();
                }

                // Go to activity list
                Fragment frag = new ActivityFragment();
                FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                trans.replace(R.id.frame, frag);
                trans.commit();
            }
        });

        // Delete Button: delete current activity data
        mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean insertStat = activityDAO.deleteActivity(mActivityId);

                // Toast message for user (success or not)
                if (insertStat == true)
                {
                    Snackbar.make(fLayout,"Activity added/updated successfully", Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    Snackbar.make(fLayout,"Activity not added/updated", Snackbar.LENGTH_SHORT).show();
                }

                Fragment frag = new ActivityFragment();
                FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                trans.replace(R.id.frame, frag);
                trans.commit();
            }
        });

        // Cancel Button: Go back to activity list
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = new ActivityFragment();
                FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                trans.replace(R.id.frame, frag);
                trans.commit();
            }
        });

        // Get selected activity ID from previous activity
        Bundle bundle = this.getArguments();
        mActivityId = 0;
        if (bundle != null) {
            mActivityId = bundle.getInt("ACTIVITY_ID", 0);
        }

        // If activity id exists, get activity data from DB, and enable Delete button
        // If not, disable delete button
        if (mActivityId != 0)
        {
            getActivity(mActivityId);
            mBtnDelete.setVisibility(View.VISIBLE);
        }
        else
        {
            mBtnDelete.setVisibility(View.INVISIBLE);
        }

        return v;
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getCategoryList() {

        // DataBase Helper Class for querying activity data
        Cursor cursor;

        // Get cursor for getting a list
        cursor = categoryDAO.getCategories("");

        // If no data, Set default Category
        if (cursor.getCount() == 0)
        {
            Category category = new Category(0, "Income");
            categoryDAO.insertCategory(category);
            category = new Category(0, "Food");
            categoryDAO.insertCategory(category);
        }

        if (cursor.moveToFirst()) {
            do {
                categoryListId.add(cursor.getInt(0));
                categoryList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
    }

    /**
     * Search corresponding to activity ID, and then Display a result
     */
    private void getActivity(int activityId)
    {
        final SimpleMoneyTracerApplication application;
        application = ((SimpleMoneyTracerApplication) getActivity().getApplication());

        // DataBase Helper Class for querying activity data
        Cursor cursor;

        // Get cursor for getting a list
        cursor = activityDAO.getActivities("WHERE id = " + activityId);

        ActivityM activityObj = new ActivityM();

        // If no data, Display Error message
        if (cursor.getCount() == 0)
        {
            Snackbar.make(fLayout,"No Record", Snackbar.LENGTH_SHORT).show();
        }
        else
        {
            if (cursor.moveToFirst())
            {
                // Set found activity data to View UI
                double amount = cursor.getDouble(1);
                if (amount < 0) {
                    amount = -amount;
                    mChkIncome.setChecked(false);
                } else {
                    mChkIncome.setChecked(true);
                }

                //mEdtAmount.setText(FormatUtil.getCurrencyString(amount));
                mEdtAmount.setText(String.valueOf(amount));

                mCategoryId = cursor.getInt(2);
                Date date = new Date(cursor.getLong(3));
                mDate = date;
                mEdtDate.setText(FormatUtils.getDateString(mDate));

                for (int position = 0; position < categoryListId.size(); position++)
                {
                    if (categoryListId.get(position) == mCategoryId)
                    {
                        mSpnCategory.setSelection(position);
                    }
                }
            }
        }
    }
}