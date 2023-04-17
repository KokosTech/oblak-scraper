package com.example.oblakscraper.managers;

import com.example.oblakscraper.api.RestApiUtils;
import com.example.oblakscraper.exceptions.*;
import com.example.oblakscraper.models.User;

import java.io.IOException;

public class LoginManager {
    private static void validateUsername(String username) throws InvalidUsernameException {
        if (username.length() < 2) {
            throw new InvalidUsernameException("Username must be at least 2 characters long");
        }
    }

    private static void validatePassword(String password) throws InvalidPasswordException {
        if (password.length() < 8) {
            throw new InvalidPasswordException("Password must be at least 8 characters long");
        }
    }

    private static void validateConfirmPassword(String password, String confirmPassword) throws PasswordsDoNotMatchException {
        if (!password.equals(confirmPassword)) {
            throw new PasswordsDoNotMatchException("Passwords do not match");
        }
    }

    public static User login(String username, String password) throws
            InvalidUsernameException, InvalidPasswordException,
            WrongUsernameException, WrongPasswordException,
            IOException, InterruptedException
    {
        validateUsername(username);
        validatePassword(password);

        return RestApiUtils.login(username, password);
    }

    public static void register(String username, String password, String confirmPassword) throws
            InvalidUsernameException, InvalidPasswordException, PasswordsDoNotMatchException,
            UsernameIsTakenException,
            IOException, InterruptedException
    {
        validateUsername(username);
        validatePassword(password);
        validateConfirmPassword(password, confirmPassword);

        RestApiUtils.register(username, password);
    }
}
