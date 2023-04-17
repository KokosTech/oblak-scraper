package com.example.oblakscraper.models;

public class Website {
    private final String id;
    private final Image thumbnail;
    private final Content content;

    private Website(Builder builder) {
        this.id = builder.id;
        this.thumbnail = builder.thumbnail;
        this.content = builder.content;
    }

    public static class Builder {
        private String id;
        private Image thumbnail;
        private Content content;
        private boolean isSaved;

        public Builder() {
            this.id = "";
            this.thumbnail = null;
            this.content = null;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder thumbnail(Image thumbnail) {
            this.thumbnail = thumbnail;
            return this;
        }

        public Builder content(Content content) {
            this.content = content;
            return this;
        }

        public Website build() {
            return new Website(this);
        }
    }

    public String getId() {
        return id;
    }

    public Image getThumbnail() {
        return thumbnail;
    }

    public Content getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "Website{" +
                "id='" + id + '\'' +
                ", thumbnail=" + thumbnail +
                ", content=" + content +
                '}';
    }
}