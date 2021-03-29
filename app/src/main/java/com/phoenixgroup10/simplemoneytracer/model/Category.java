package com.phoenixgroup10.simplemoneytracer.model;

public class Category {
    private int id;
    private String name;

    // Constructor without member variables
    public Category() {
    }

    // Constructor with all member variables
    public Category(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    // Setters and Getters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
