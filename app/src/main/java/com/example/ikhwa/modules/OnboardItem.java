package com.example.ikhwa.modules;

public class OnboardItem {
    private final String title;
    private final String description;
    private final int imageRes;

    public OnboardItem(String title, String description, int imageRes) {
        this.title = title;
        this.description = description;
        this.imageRes = imageRes;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getImageRes() {
        return imageRes;
    }
}

