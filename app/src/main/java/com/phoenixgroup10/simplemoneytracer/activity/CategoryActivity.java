package com.phoenixgroup10.simplemoneytracer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.phoenixgroup10.simplemoneytracer.Common;
import com.phoenixgroup10.simplemoneytracer.R;
import com.phoenixgroup10.simplemoneytracer.SimpleMoneyTracerApplication;
import com.phoenixgroup10.simplemoneytracer.adapter.CategoryAdapter;
import com.phoenixgroup10.simplemoneytracer.dao.CategoryDAO;
import com.phoenixgroup10.simplemoneytracer.model.Category;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity  implements View.OnClickListener {
    SimpleMoneyTracerApplication application;
    ArrayList<Category> models = new ArrayList<Category>();

    CategoryDAO categoryDAO;
    RecyclerView categoryListView;
    CategoryAdapter categoryAdapter;
    Button categoryAdd, categoryUpdate, categoryCancel;
    EditText etEnterName;
    int position;
    int themeID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_SimpleMoneyTracer);
        super.onCreate(savedInstanceState);
        application = ((SimpleMoneyTracerApplication) getApplication());
        categoryDAO= new CategoryDAO(application);
        setContentView(R.layout.activity_category);

        categoryListView = findViewById(R.id.category_item);
        etEnterName = findViewById(R.id.category_enter_name);
        categoryAdd = findViewById(R.id.btn_add);
        categoryUpdate = findViewById(R.id.btn_update);
        categoryCancel = findViewById(R.id.btn_cancel);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable((Color.parseColor("#001d3d"))));
        }
        categoryListView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        categoryListView.setLayoutManager(layoutManager);
        getCategories();
        categoryAdapter = new CategoryAdapter(getApplicationContext(), models,
                new CategoryAdapter.Onclick() {
                    @Override
                    public void onEvent(Category model, int pos) {
                        position = pos;
                        toggleButton(Common.ADD_INVISIBLE);
                        etEnterName.setText(model.getName());
                    }
                });

        categoryListView.setAdapter(categoryAdapter);
        categoryAdd.setOnClickListener(this);
        categoryUpdate.setOnClickListener(this);
        categoryCancel.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        String newName = etEnterName.getText().toString();
        switch (view.getId()) {
            case R.id.btn_add: {
                if(newName.trim().equals("")){
                    Snackbar.make(view, "Can not be added", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(application, "Category Name is Required!", Toast.LENGTH_SHORT).show();
                    etEnterName.setFocusable(true);
                } else if (isDuplicatedName(newName)) {
                    Snackbar.make(view, "Can not be added", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(application, "Duplicated category name!", Toast.LENGTH_SHORT).show();
                } else{
                        if(insertCategory(newName))  etEnterName.setText("");
                }
            }
            break;
            case R.id.btn_update: {
                if(newName.trim().isEmpty()){
                    Snackbar.make(view, "Can not be updated", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(application, "Category Name is Required!", Toast.LENGTH_SHORT).show();
                }
                else if(isDuplicatedName(newName)){
                    Snackbar.make(view, "Can not be updated", Snackbar.LENGTH_LONG).show();
                    Toast.makeText(application, "Duplicated category name!", Toast.LENGTH_SHORT).show();
                }
                else{
                    categoryAdapter.notifyDataSetChanged();
                    toggleButton(Common.ADD_VISIBLE);
                    models.get(position).setName(etEnterName.getText().toString());
                    if(udpateCategory(models.get(position))){
                        Toast.makeText(application, "Successfully updated!", Toast.LENGTH_SHORT).show();
                        etEnterName.setText("");
                        hideInputForm(view);
                    }
                }
            }
            break;
            case R.id.btn_cancel: {
                etEnterName.setText("");
                toggleButton(Common.ADD_VISIBLE);
                hideInputForm(view);
            }
            break;
        }
    }

    private void hideInputForm(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
    }

    private void toggleButton(int val){

        if(val == Common.ADD_VISIBLE){
            categoryAdd.setVisibility(View.VISIBLE);
            categoryUpdate.setVisibility(View.INVISIBLE);
            categoryCancel.setVisibility(View.INVISIBLE);
        }
        else{
            categoryAdd.setVisibility(View.INVISIBLE);
            categoryUpdate.setVisibility(View.VISIBLE);
            categoryCancel.setVisibility(View.VISIBLE);
        }
    }
    private boolean isDuplicatedName(String name){
        Cursor c = categoryDAO.getCategories("where upper(name) = '" + name.toUpperCase() + "'");
        return (c.getCount() > 0)? true : false;
    }

    private void getCategories(){
        Cursor cursor = categoryDAO.getCategories("");

        if (cursor.getCount() == 0)
        {
            Toast.makeText(application, "No Category", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (cursor.moveToFirst())
            {
                do {
                    Category c = new Category();
                    c.setId(cursor.getInt(0));
                    c.setName(cursor.getString(1));
                    models.add(c);
                } while (cursor.moveToNext());
            }
        }
    }

    private boolean insertCategory(String name) {
        Category c = new Category();
        c.setName(name);
        int newId = (int)categoryDAO.insertCategory(c);
        if(newId != -1){
            c.setId(newId);
            models.add(c);
            categoryAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    private boolean udpateCategory(Category c) {
        boolean result = categoryDAO.updateCategory(c);
        categoryAdapter.notifyDataSetChanged();

        return result;
    }

    /**
     * Home button: back to main activity
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean ret = true;

        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            default:
                ret = super.onOptionsItemSelected(item);
                break;
        }

        return ret;
    }
}