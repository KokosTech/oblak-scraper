package com.example.oblakscraper.controllers;

import com.example.oblakscraper.api.RestApiUtils;
import com.example.oblakscraper.exceptions.CookieExpired;
import com.example.oblakscraper.models.Content;
import com.example.oblakscraper.models.Website;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;

public class WebsiteContainerController {
    private HomeController homeController;
    private Website website;

    @FXML
    ImageView thumbnail;
    @FXML
    Label title;

    @FXML
    protected void onWebsiteClicked() throws IOException {
        try {
            Content content = RestApiUtils.getWebsiteContent(website);
            homeController.switchToWebsitePage(content);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (CookieExpired e) {
            homeController.switchToLoginPage();
        }
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    public void setWebsite(Website website) {
        this.website = website;

        if (website.getThumbnail() == null) {
            System.out.println("Thumbnail is null");
            this.thumbnail.setImage(new Image("file:src/main/resources/com/example/oblakscraper/view/images/default-thumbnail.png"));
        } else {
            this.thumbnail.setImage(new Image(website.getThumbnail().getUrl()));
        }

        this.title.setText(website.getContent().getTitle());
    }
}
