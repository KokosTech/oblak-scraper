package com.example.oblakscraper.dao;

import com.example.oblakscraper.models.Content;
import com.example.oblakscraper.models.Image;
import com.example.oblakscraper.models.Website;
import com.example.oblakscraper.sqlite.JDBCUtils;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebsiteDao {
    private static final String INSERT_INTO_WEBSITES =
            "INSERT INTO Websites (id, owner_id, title, url, text) VALUES (?, ?, ?, ?, ?)";

    private static final String INSERT_INTO_IMAGES =
            "INSERT INTO Images (website_id, url, alt) VALUES (?, ?, ?)";

    private static final String SELECT_ALL_WEBSITES =
            "SELECT * FROM Websites";

    private static final String SELECT_FROM_WEBSITES =
            "SELECT * FROM Websites WHERE id = ?";

    private static final String SELECT_FROM_IMAGES =
            "SELECT * FROM Images WHERE website_id = ?";


    private void insertImages(String websiteId, List<Image> images) throws SQLException, URISyntaxException, ClassNotFoundException {
        try (Connection connection = JDBCUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_IMAGES);

            for (Image image: images) {
                preparedStatement.setString(1, websiteId);
                preparedStatement.setString(2, image.getUrl());
                preparedStatement.setString(3, image.getAlt());

                preparedStatement.executeUpdate();
            }
        }
    }

    public void insertWebsite(Website website) throws SQLException, URISyntaxException, ClassNotFoundException {
        try (Connection connection = JDBCUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_WEBSITES);

            System.out.println(website.getContent().getText());

            preparedStatement.setString(1, website.getId());
            preparedStatement.setString(2, website.getContent().getOwnerId());
            preparedStatement.setString(3, website.getContent().getTitle());
            preparedStatement.setString(4, website.getContent().getUrl());
            preparedStatement.setString(5, website.getContent().getText());

            preparedStatement.executeUpdate();
        }

        insertImages(website.getId(), website.getContent().getImages());
    }

    public Set<Website> getAllWebsites(String userId) throws SQLException, URISyntaxException, ClassNotFoundException {
        try (Connection connection = JDBCUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_WEBSITES);
            ResultSet resultSet = preparedStatement.executeQuery();

            Set<Website> websites = new HashSet<>();

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                List<Image> images = getImages(id);

                Content content = new Content.Builder()
                        .title(resultSet.getString("title"))
                        .url(resultSet.getString("url"))
                        .ownerId(userId)
                        .build();

                websites.add(new Website.Builder()
                        .id(resultSet.getString("id"))
                        .thumbnail(images.stream().findFirst().orElse(null))
                        .content(content)
                        .build());
            }

            return websites;
        }
    }

    private List<Image> getImages(String websiteId) throws SQLException, URISyntaxException, ClassNotFoundException {
        try (Connection connection = JDBCUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_FROM_IMAGES);

            preparedStatement.setString(1, websiteId);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Image> images = new ArrayList<>();

            while (resultSet.next()) {
                images.add(new Image(
                    resultSet.getString("url"),
                    resultSet.getString("alt")
                ));
            }

            return images;
        }
    }

    public Content getWebsiteContent(Website website) throws SQLException, URISyntaxException, ClassNotFoundException {
        List<Image> images = getImages(website.getContent().getOwnerId());

        try (Connection connection = JDBCUtils.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_FROM_WEBSITES);

            preparedStatement.setString(1, website.getId());
            ResultSet resultSet = preparedStatement.executeQuery();

            Content content = null;

            if (resultSet.next()) {
                content =  new Content.Builder()
                        .ownerId(resultSet.getString("owner_id"))
                        .title(resultSet.getString("title"))
                        .url(resultSet.getString("url"))
                        .text(resultSet.getString("text"))
                        .images(images)
                        .build();
            }

            return content;
        }
    }
}