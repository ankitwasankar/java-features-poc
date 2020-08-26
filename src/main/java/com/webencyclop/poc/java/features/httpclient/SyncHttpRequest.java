package com.webencyclop.poc.java.features.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class SyncHttpRequest {
    private static HttpClient client;

    public static void main(String[] args) throws IOException, URISyntaxException {
        client = HttpClient.newHttpClient();
        Files.lines(Path.of(Objects.requireNonNull(SyncHttpRequest.class.getClassLoader().getResource("urls.txt")).toURI()))
                .map(SyncHttpRequest::validateLink)
                .forEach(System.out::println);
    }

    static String validateLink(String link) {
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(link))
                .GET()
                .build();
        try {
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            boolean isSuccess = response.statusCode() >= 200 && response.statusCode() < 300;
            return String.format("%s -> %s", link, isSuccess);
        } catch (IOException | InterruptedException e) {
            return String.format("%s -> %s", link, false);
        }
    }
}
