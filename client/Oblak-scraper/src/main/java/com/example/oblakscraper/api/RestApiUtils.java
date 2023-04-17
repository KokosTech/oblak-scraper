package com.example.oblakscraper.api;

import com.example.oblakscraper.exceptions.CookieExpired;
import com.example.oblakscraper.exceptions.UsernameIsTakenException;
import com.example.oblakscraper.exceptions.WrongPasswordException;
import com.example.oblakscraper.exceptions.WrongUsernameException;
import com.example.oblakscraper.models.Content;
import com.example.oblakscraper.models.Image;
import com.example.oblakscraper.models.User;
import com.example.oblakscraper.models.Website;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;


public class RestApiUtils {
    private static final String BASE_URL = "http://mail.kaloyan.tech:20000/api/v1";
    private static final CookieManager cookieManager = new CookieManager();
    private static final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(cookieManager)
            .build();

    public static User login(String username, String password) throws IOException, InterruptedException, WrongUsernameException, WrongPasswordException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                    "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }"
                ))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());

        switch (response.statusCode()) {
            case 200 -> {
                response.headers().map().get("Set-Cookie").forEach(cookie -> {
                    HttpCookie httpCookie = HttpCookie.parse(cookie).get(0);
                    cookieManager.getCookieStore().add(URI.create(BASE_URL), httpCookie);
                });

                JsonObject userJson = JsonParser.parseString(response.body()).getAsJsonObject();

                User user = new User(userJson.get("id").getAsString(), username, password);
                user.setWebsites(fetchWebsites(user.getId()));

                return user;
            }
            case 401 -> throw new WrongUsernameException("Wrong username");
            case 402 -> throw new WrongPasswordException("Wrong password");
            default -> throw new IOException("Unexpected status code: " + response.statusCode());
        }
    }

    public static void register(String username, String password) throws IOException, InterruptedException, UsernameIsTakenException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/signup"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                    "{ \"username\": \"" + username + "\", \"password\": \"" + password + "\" }"
                ))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        switch (response.statusCode()) {
            case 201 -> System.out.println("Successfully registered");
            case 400 -> throw new UsernameIsTakenException("Username is taken");
            default -> throw new IOException("Unexpected status code: " + response.statusCode());
        }
    }

    private static Set<Website> fetchWebsites(String userId) throws IOException, InterruptedException {
        String cookie = cookieManager.getCookieStore().getCookies().get(0).toString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/get/websites/" + userId))
                .header("Content-Type", "application/json")
                .header("Cookie", cookie)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        switch (response.statusCode()) {
            case 200 -> {
                Set<Website> websites = new HashSet<>();

                JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray jsonArray = jsonObject.getAsJsonArray("websites");

                jsonArray.forEach(jsonElement -> {
                    JsonObject websiteJson = jsonElement.getAsJsonObject();

                    Image thumbnail = null;

                    System.out.println(websiteJson.get("thumbnail"));

                    if (!websiteJson.get("thumbnail").isJsonNull()) {
                        JsonObject thumbnailJson = websiteJson.get("thumbnail").getAsJsonObject();

                        thumbnail = new Image(
                                thumbnailJson.get("url").getAsString(),
                                thumbnailJson.get("alt").getAsString()
                        );
                    }

                    Content content = new Content.Builder()
                            .title(websiteJson.get("title").getAsString())
                            .url(websiteJson.get("url").getAsString())
                            .ownerId(userId)
                            .isSaved(true)
                            .build();

                    websites.add(new Website.Builder()
                            .id(websiteJson.get("id").getAsString())
                            .thumbnail(thumbnail)
                            .content(content)
                            .build());
                });

                return websites;
            }
            default -> throw new IOException("Unexpected status code: " + response.statusCode());
        }
    }

    public static Website addWebsite(Content content) throws IOException, InterruptedException, CookieExpired {
        HttpClient client = HttpClient.newHttpClient();

        // не може по-красиво
        List<Map<String, String>> images = new ArrayList<>();

        for (Image image: content.getImages()) {
            images.add(Map.of(
                    "url", image.getUrl(),
                    "alt", image.getAlt()
            ));
        }

        Map<String, Object> body = Map.of(
                "title", content.getTitle(),
                "url", content.getUrl(),
                "text", content.getText(),
                "images", images,
                "owner_id", Integer.parseInt(content.getOwnerId())
        );

        Gson gson = new Gson();
        String json = gson.toJson(body);

        System.out.println(json);

        String cookie = cookieManager.getCookieStore().getCookies().get(0).toString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/post/website"))
                .header("Content-Type", "application/json")
                .header("Cookie", cookie)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();


        System.out.println(request.headers());

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        switch (response.statusCode()) {
            case 200 -> {
                JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

                Content newContent = new Content.Builder()
                        .title(content.getTitle())
                        .url(content.getUrl())
                        .ownerId(content.getOwnerId())
                        .isSaved(true)
                        .build();

                return new Website.Builder()
                        .id(jsonObject.get("id").getAsString())
                        .thumbnail(content.getImages().stream().findFirst().orElse(null))
                        .content(newContent)
                        .build();
            }
            case 403 -> throw new CookieExpired("Cookie expired");
            default -> throw new IOException("Unexpected status code: " + response.statusCode());
        }

    }

    public static Content getWebsiteContent(Website website) throws IOException, InterruptedException, CookieExpired {
        String cookie = cookieManager.getCookieStore().getCookies().get(0).toString();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/get/website/" + website.getId()))
                .header("Content-Type", "application/json")
                .header("Cookie", cookie)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();

        switch (jsonObject.get("status").getAsInt()) {
            case 200 -> {
                JsonObject websiteJson = jsonObject.getAsJsonObject("website");

                JsonArray imagesJson = websiteJson.getAsJsonArray("images");
                List<Image> images = new ArrayList<>();

                imagesJson.forEach(imageJson -> {
                    JsonObject image = imageJson.getAsJsonObject();
                    images.add(new Image(image.get("url").getAsString(), image.get("alt").getAsString()));
                });

                return new Content.Builder()
                        .title(websiteJson.get("title").getAsString())
                        .url(websiteJson.get("url").getAsString())
                        .text(websiteJson.get("text").getAsString())
                        .images(images)
                        .ownerId(websiteJson.get("owner_id").getAsString())
                        .isSaved(true)
                        .build();
            }
            case 403 -> throw new CookieExpired("Cookie expired");
            default -> throw new IOException("Unexpected status code: " + response.statusCode());
        }
    }
}
