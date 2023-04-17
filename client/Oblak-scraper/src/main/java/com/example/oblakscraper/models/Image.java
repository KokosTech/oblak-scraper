package com.example.oblakscraper.models;

public class Image {
    private final String url;
    private final String alt;

    public Image(String url, String alt) {
        this.url = url;
        this.alt = alt;
    }

    public String getUrl() {
        return url;
    }

    public String getAlt() {
        return alt;
    }

    @Override
    public String toString() {
        return "Image{" +
                "url='" + url + '\'' +
                ", alt='" + alt + '\'' +
                '}';
    }
}
