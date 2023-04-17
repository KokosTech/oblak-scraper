package com.example.oblakscraper.controllers;

import com.example.oblakscraper.Main;
import com.example.oblakscraper.api.RestApiUtils;
import com.example.oblakscraper.dao.WebsiteDao;
import com.example.oblakscraper.exceptions.CookieExpired;
import com.example.oblakscraper.models.Content;
import com.example.oblakscraper.models.User;
import com.example.oblakscraper.models.Website;
import com.example.oblakscraper.scraper.Scraper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Objects;

public class HomeController {
    private User user;
    private final WebsiteDao websiteDao = new WebsiteDao();
    private Content content;

    @FXML
    private SplitPane homePage;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private TilePane grid;
    @FXML
    private VBox websitePage;
    @FXML
    private Button backButton;
    @FXML
    private TextField URLField;
    @FXML
    private TextArea textField;
    @FXML
    private TilePane imagesGrid;
    @FXML
    private ScrollPane imagesScroll;
    @FXML
    private Button saveButton;
    @FXML
    private HBox searchBar;

    public void switchToLoginPage() throws IOException {
        searchField.setText("");
        grid.getChildren().clear();

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/login_page.fxml"));
        Parent root = fxmlLoader.load();

        Stage stage = (Stage) (homePage.getScene().getWindow());
        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.show();
    }


    public void switchToWebsitePage(Content content) throws IOException {
        searchField.setText("");

        URLField.setText(content.getUrl());
        textField.setText(content.getText());

        content.getImages().forEach(image -> {
            ImageView imageView = new ImageView(new Image(image.getUrl()));
            imageView.setFitWidth(220);
            imageView.setFitHeight(180);
            imageView.setPreserveRatio(true);
            imagesGrid.getChildren().add(imageView);
        });

        backButton.setGraphic(new ImageView(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("images/back_icon.png")))));

        websitePage.toFront();
    }

    @FXML
    protected void onSearchButtonClick() throws IOException {
        String searchURL = searchField.getText();
        content = Scraper.scrape(searchURL);
        content.PrintLinksThatCantBeDisplayed();
        switchToWebsitePage(content.GetWorkingContent());
    }

    @FXML
    protected void onBackButtonClick() {
        imagesGrid.getChildren().clear();
        homePage.toFront();
    }

    @FXML
    protected void onSaveButtonClick() throws IOException {
        content.setOwnerId(user.getId());

        try {
            Website website = RestApiUtils.addWebsite(content.GetWorkingContent());
            System.out.println(website);
            user.addWebsite(website);
            addToGrid(website);
            websiteDao.insertWebsite(website);
            onBackButtonClick();
        } catch (IOException | InterruptedException | SQLException | URISyntaxException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (CookieExpired e) {
            switchToLoginPage();
        }
    }

    public void addToGrid(Website website) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/website_container.fxml"));
        grid.getChildren().add(fxmlLoader.load());

        WebsiteContainerController websiteContainerController = fxmlLoader.getController();
        websiteContainerController.setWebsite(website);
        websiteContainerController.setHomeController(this);
    }

    private void renderWebsites() throws IOException {
        user.getWebsites().forEach(website -> {
            try {
                addToGrid(website);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setUser(User user) throws IOException {
        this.user = user;
        renderWebsites();
    }

    public void setSearchButtonIcon() {
        searchButton.setGraphic(new ImageView(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("images/search_icon.png")))));
    }
}
