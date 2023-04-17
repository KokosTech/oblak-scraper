package com.example.oblakscraper.models;

import java.util.ArrayList;
import java.util.List;

public class Content {
    private final String title;
    private final String url;
    private final String text;
    private final List<Image> images;
    private String ownerId;
    private boolean isSaved;

    private Content(Builder builder) {
        this.title = builder.title;
        this.url = builder.url;
        this.text = builder.text;
        this.images = builder.images;
        this.ownerId = builder.ownerId;
        this.isSaved = builder.isSaved;
    }

    public static class Builder {
        private String title;
        private String url;
        private String text;
        private List<Image> images;
        private String ownerId;
        private boolean isSaved;

        public Builder() {
            this.title = null;
            this.url = null;
            this.text = null;
            this.images = new ArrayList<>();
            this.ownerId = "";
            this.isSaved = false;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder images(List<Image> images) {
            this.images = images;
            return this;
        }

        public Builder ownerId(String ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Builder isSaved(boolean isSaved) {
            this.isSaved = isSaved;
            return this;
        }

        public Content build() {
            return new Content(this);
        }
    }

    // This method is used to print all the links that can not be displayed by javafx. They will be displayed in the console.
    public void PrintLinksThatCantBeDisplayed() {
        System.out.println("Links that can not be visualised by javafx : ");
        for (Image i: images){
            if (!i.getUrl().matches("(.*\\.(jpeg|jpg|png|bmp|gif)$).*$")) {
                System.out.println(i);
            }
        }
    }


    // This method is used to get a content object that only contains images that can be displayed by javafx
    public Content GetWorkingContent(){
        ArrayList<Image> newImages = new ArrayList<>();

        for(Image i : images){
            if(i.getUrl().matches("(.*\\.(jpeg|jpg|png|bmp|gif)$).*$")){
                newImages.add(i);
            }
        }

        return new Builder()
                .title(this.title)
                .url(this.url)
                .ownerId(this.ownerId)
                .isSaved(this.isSaved)
                .images(newImages)
                .text(this.text)
                .build();
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getText() {
        return text;
    }

    public List<Image> getImages() {
        return images;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    @Override
    public String toString() {
        return "Content{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", text='" + text + '\'' +
                ", images=" + images +
                ", ownerId='" + ownerId + '\'' +
                ", isSaved=" + isSaved +
                '}';
    }
}

