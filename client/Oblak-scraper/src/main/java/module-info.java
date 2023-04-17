module com.example.oblakscraper {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.jsoup;
    requires com.google.gson;
    requires org.xerial.sqlitejdbc;

    opens com.example.oblakscraper to javafx.fxml;
    exports com.example.oblakscraper;
    exports com.example.oblakscraper.controllers;
    opens com.example.oblakscraper.controllers to javafx.fxml;
    opens com.example.oblakscraper.models to com.google.gson;
}