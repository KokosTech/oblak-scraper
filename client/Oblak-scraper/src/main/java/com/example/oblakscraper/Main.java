package com.example.oblakscraper;

import com.example.oblakscraper.controllers.LoginController;
import com.example.oblakscraper.dao.WebsiteDao;
import com.example.oblakscraper.sqlite.JDBCUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException, URISyntaxException, ClassNotFoundException {
        JDBCUtils.createDatabaseStructure();
        //JDBCUtils.deleteDatabase();
        WebsiteDao websiteDao = new WebsiteDao();
        websiteDao.getAllWebsites("8").forEach(System.out::println);
        JDBCUtils.createDatabaseStructure();

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/login_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 540);

        LoginController loginController = fxmlLoader.getController();
        loginController.setBackButtonIcon();

        stage.setTitle("Oblak-sraper");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}