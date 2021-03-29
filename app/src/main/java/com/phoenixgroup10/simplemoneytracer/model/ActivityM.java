package com.phoenixgroup10.simplemoneytracer.model;

import java.util.Date;

public class ActivityM {
    private int id;
    private double amount;
    private int categoryId;
    private Date date;

    // Constructor without member variables
    public ActivityM() {
    }

    // Constructor with all member variables
    public ActivityM(int id, double amount, int categoryId, Date date)
    {
        this.id = id;
        this.amount = amount;
        this.categoryId = categoryId;
        this.date = date;
    }

    // Setters and Getters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getDateEpoch() {
        return date.getTime();
    }

    public void setDateEpoch(long dateEpoch) {
        this.date = new Date(dateEpoch);
    }
}
