package com.duta.library.model;

public class ListData {

    private String text;
    private String imagePath;

    public ListData(String text, String imagePath) {
        this.text = text;
        this.imagePath = imagePath;
    }

    public String getText() {
        return text;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String toString() {
        return text;
    }
}
