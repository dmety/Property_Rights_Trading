package com.example.app.components;

public class PropertyItem {
    private String title;
    private String description;

    public PropertyItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
