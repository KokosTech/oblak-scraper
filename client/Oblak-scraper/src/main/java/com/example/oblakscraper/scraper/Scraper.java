package com.example.oblakscraper.scraper;

import com.example.oblakscraper.models.Content;
import com.example.oblakscraper.models.Image;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Scraper {
    public static Content scrape(String url) throws IOException {
        List<Image> imagesResults = new ArrayList<>();
        String textResults;

        Document doc = Jsoup.connect(url).get();
        Elements images = doc.select("img");

        for (Element image : images) {
            String imageLink = image.absUrl("src");
            imagesResults.add(new Image(imageLink, image.attr("alt")));

        }

        textResults = doc.text();

        return new Content.Builder()
                .title(doc.title())
                .url(url)
                .text(textResults)
                .images(imagesResults)
                .build();
    }
}