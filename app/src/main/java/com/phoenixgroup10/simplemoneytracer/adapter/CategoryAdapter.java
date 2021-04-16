package com.phoenixgroup10.simplemoneytracer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.phoenixgroup10.simplemoneytracer.Common;
import com.phoenixgroup10.simplemoneytracer.SimpleMoneyTracerApplication;
import com.phoenixgroup10.simplemoneytracer.dao.ActivityDAO;
import com.phoenixgroup10.simplemoneytracer.dao.CategoryDAO;
import androidx.recyclerview.widget.RecyclerView;

import com.phoenixgroup10.simplemoneytracer.R;
import com.phoenixgroup10.simplemoneytracer.model.Category;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    Context context;
    ArrayList<Category> models;
    Onclick onclick;
    public interface Onclick {
        void onEvent(Category model,int pos);
    }
    public CategoryAdapter(Context context, ArrayList<Category> models, Onclick onclick) {
        this.context = context;
        this.models = models;
        this.onclick = onclick;
    }
    View view;
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.from(parent.getContext()).inflate(R.layout.activity_category_record, parent, false);

        CategoryViewHolder cvViewHolder = new CategoryViewHolder(view);

        int themeID = Common.getThemeId(this.context, "category");
        if(themeID == R.style.Category_darkTheme) {
            cvViewHolder.itemName.setTextColor(Color.parseColor("#C3C3C0"));
        }
        return cvViewHolder;
    }
    @Override
    public void onBindViewHolder(CategoryAdapter.CategoryViewHolder holder, final int position) {
        final Category model = models.get(position);
        if (model.getName() != null) {
            holder.itemName.setText(model.getName());
        }
       /* holder.itemName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                holder.itemName.setTextColor(Color.parseColor("#00aaff"));
                holder.removeImg.setBackgroundResource(R.drawable.day_picker_item_background);
            }
        });*/
        holder.removeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryDAO cd = new CategoryDAO((SimpleMoneyTracerApplication) context.getApplicationContext());
                ActivityDAO ad = new ActivityDAO((SimpleMoneyTracerApplication) context.getApplicationContext());
                int id = models.get(position).getId();
                Cursor c = ad.getActivities("where category_id = " + String.valueOf(id));
                if(c.getCount() > 0) {
                    Snackbar.make(view, "Some transaction was assigned", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(context, "Can not be deleted", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(cd.deleteCategory(models.get(position).getId())){
                        models.remove(position);
                        notifyDataSetChanged();
                    }
                    else{
                        Snackbar.make(view, "Can not be deleted", Snackbar.LENGTH_LONG).show();
                        Toast.makeText(context, "Can not be deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onclick.onEvent(model,position);
            }
        });
    }
    @Override
    public int getItemCount() {
        return models.size();
    }
    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        ImageView removeImg;
        LinearLayout llItem;
        public CategoryViewHolder(View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.category_name);
            removeImg = itemView.findViewById(R.id.img_remove);
            llItem = itemView.findViewById(R.id.category_item);

        }
    }
}
