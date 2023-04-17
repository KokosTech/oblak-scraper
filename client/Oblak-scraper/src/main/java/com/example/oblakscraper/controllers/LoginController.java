package com.example.oblakscraper.controllers;

import com.example.oblakscraper.Main;
import com.example.oblakscraper.exceptions.*;
import com.example.oblakscraper.managers.LoginManager;
import com.example.oblakscraper.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class LoginController {
    @FXML
    private VBox loginBox;
    @FXML
    private VBox signupBox;
    @FXML
    private TextField loginUsername;
    @FXML
    private Label loginUsernameError;
    @FXML
    private PasswordField loginPassword;
    @FXML
    private Label loginPasswordError;
    @FXML
    private TextField signupUsername;
    @FXML
    private Label signupUsernameError;
    @FXML
    private TextField signupPassword;
    @FXML
    private Label signupPasswordError;
    @FXML
    private TextField signupConfirmPassword;
    @FXML
    private Label signupConfirmPasswordError;
    @FXML
    private Button loginButton;
    @FXML
    private Button backButton;

    private void switchToHomePage(ActionEvent event, User user) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/home_page.fxml"));
        Parent root = fxmlLoader.load();

        HomeController homeController = fxmlLoader.getController();
        homeController.setSearchButtonIcon();
        homeController.setUser(user);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void onLoginButtonClick(ActionEvent event) {
        loginUsernameError.setText("");
        loginPasswordError.setText("");

        String username = loginUsername.getText();
        String password = loginPassword.getText();

        try {
            User user = LoginManager.login(username, password);
            switchToHomePage(event, user);
        } catch (InvalidUsernameException | WrongUsernameException e) {
            loginUsernameError.setText(e.getMessage());
            loginUsername.clear();
        } catch (InvalidPasswordException | WrongPasswordException e) {
            loginPasswordError.setText(e.getMessage());
            loginPassword.clear();
        } catch (IOException | InterruptedException e) {
            //Platform.exit();
        }

        loginBox.requestFocus();
    }

    @FXML
    protected void onSignUpButtonClick() {
        loginUsername.clear();
        loginPassword.clear();

        loginUsernameError.setText("");
        loginPasswordError.setText("");

        loginBox.requestFocus();
        signupBox.toFront();
    }

    @FXML
    protected void onBackButtonClick() {
        signupUsername.clear();
        signupPassword.clear();
        signupConfirmPassword.clear();

        signupUsernameError.setText("");
        signupPasswordError.setText("");
        signupConfirmPasswordError.setText("");

        signupBox.requestFocus();
        loginBox.toFront();
    }

    @FXML
    protected void onRegisterButtonClick() {
        signupUsernameError.setText("");
        signupPasswordError.setText("");
        signupConfirmPasswordError.setText("");

        String username = signupUsername.getText();
        String password = signupPassword.getText();
        String confirmPassword = signupConfirmPassword.getText();

       try {
           LoginManager.register(username, password, confirmPassword);

           onBackButtonClick();

           loginUsername.setText(username);
           loginPassword.setText(password);
           loginButton.requestFocus();
       } catch (InvalidUsernameException | UsernameIsTakenException e) {
           signupUsernameError.setText(e.getMessage());
           signupUsername.clear();
       } catch (InvalidPasswordException e) {
           signupPasswordError.setText(e.getMessage());
           signupPassword.clear();
       } catch (PasswordsDoNotMatchException e) {
           signupConfirmPasswordError.setText(e.getMessage());
           signupConfirmPassword.clear();
       } catch (IOException | InterruptedException e) {
           //Platform.exit();
       }

        signupBox.requestFocus();
    }

    public void setBackButtonIcon() throws FileNotFoundException {
        backButton.setGraphic(new ImageView(new Image(Objects.requireNonNull(Main.class.getResourceAsStream("images/back_icon.png")))));
    }
}
