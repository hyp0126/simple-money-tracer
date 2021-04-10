package com.phoenixgroup10.simplemoneytracer.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.phoenixgroup10.simplemoneytracer.R;
import com.phoenixgroup10.simplemoneytracer.SimpleMoneyTracerApplication;
import com.phoenixgroup10.simplemoneytracer.dao.ActivityDAO;
import com.phoenixgroup10.simplemoneytracer.dao.CategoryDAO;
import com.phoenixgroup10.simplemoneytracer.helper.FormatUtils;
import com.phoenixgroup10.simplemoneytracer.model.ActivityM;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActivityListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // List variable for manipulating data
    private List<ActivityM> mList;
    private onClickListener onClickListener;

    private CategoryDAO categoryDAO;

    public ActivityListAdapter(List<ActivityM> activities, Context context) {
        super();
        mList = activities;
        SimpleMoneyTracerApplication application;
        categoryDAO = new CategoryDAO((SimpleMoneyTracerApplication) context.getApplicationContext());
    }

    /**
     * ViewHolder for looking list data
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTxtAmount;
        public TextView mTxtCategory;
        public TextView mTxtDate;
        public Button btnEditActivity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Set UI instance variables
            mTxtAmount = (TextView) itemView.findViewById(R.id.txtAmount);
            mTxtCategory = (TextView) itemView.findViewById(R.id.txtCategory);
            mTxtDate = (TextView) itemView.findViewById(R.id.txtDate);
            btnEditActivity = (Button) itemView.findViewById(R.id.btnEditActivity);

            btnEditActivity.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        // Click Event: Set clicked position, call user defined onClick Function
        @Override
        public void onClick(View v) {
            onClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    /**
     * Set User defined onClickListner to this class
     *
     * @param onClickListener interface for processing click events
     */
    public void setOnItemClickListener(onClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * Interface for click event procedures
     */
    public interface onClickListener {
        void onItemClick(int position, View v);
    }

    /**
     * Connecting ViewHolder to layout
     *
     * @param parent   parent Viewgroup
     * @param viewType view type
     * @return created ViewHolder
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating layout for recycler view layout and Create Viewholder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_record_layout, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Loading data to layout (selected position)
     *
     * @param holder   viewholder
     * @param position data position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ActivityM activityAdapter = mList.get(position);

        // Set view text data
        double amount = activityAdapter.getAmount();
        if (amount < 0) amount = -amount;

        ((ViewHolder) holder).mTxtAmount.setText(FormatUtils.getCurrencyString(amount));
        int categoryId = activityAdapter.getCategoryId();

        Cursor cursor = categoryDAO.getCategories("WHERE id = " + categoryId);
        String categoryName = "No Category";
        if (cursor.moveToFirst()) {
            categoryName = cursor.getString(1);
        };

        ((ViewHolder) holder).mTxtCategory.setText(categoryName);
        ((ViewHolder) holder).mTxtDate.setText(FormatUtils.getDateString(activityAdapter.getDate()));
    }

    /**
     * Get current number of activity data
     *
     * @return Data count
     */
    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * Get activity ID coressponding to position
     *
     * @param position selected position
     * @return activity Id
     */
    public int getActivityId(int position) {
        return mList.get(position).getId();
    }
}