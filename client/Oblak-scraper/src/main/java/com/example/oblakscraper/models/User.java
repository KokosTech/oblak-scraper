package com.example.oblakscraper.models;

import java.util.HashSet;
import java.util.Set;

public class User {
    private final String id;
    private String username;
    private String password;
    private Set<Website> websites;

    public User(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.websites = new HashSet<>();
    }

    public void addWebsite(Website website) {
        this.websites.add(website);
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Website> getWebsites() {
        return websites;
    }

    public void setWebsites(Set<Website> websites) {
        this.websites = websites;
    }
}