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
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BasicAsyncHttpRequest {

    private static HttpClient client;

    public static void main(String[] args) throws IOException, URISyntaxException {
        client = HttpClient.newHttpClient();

        // Get list of futures Async way.
        var futures = Files.lines(Path.of(Objects.requireNonNull(BasicSyncHttpRequest.class.getClassLoader().getResource("urls.txt")).toURI()))
                .map(BasicAsyncHttpRequest::validateLink)
                .collect(Collectors.toList());
        // print all
        futures.stream().map(CompletableFuture::join).forEach(System.out::println);

    }

    private static CompletableFuture<String> validateLink(String link) {

        // 1. create basic HttpRequest
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(link))
                .GET()
                .build();

        // 2. Async call
        CompletableFuture<HttpResponse<Void>> response =
                client.sendAsync(request, HttpResponse.BodyHandlers.discarding());

        // lambda to call when successful
        Function<HttpResponse<Void>, String> successFunction = (resp) -> {
            boolean isSuccess = resp.statusCode() >= 200 && resp.statusCode() < 300;
            return String.format("%s -> %s", resp.uri(), isSuccess);
        };
        // lambda to call when exception
        Function<Throwable, String> exceptionFunction = (exception) -> String.format("%s -> %s", link, false);

        // 3. what to do on call-backs
        return response.thenApply(successFunction).exceptionally(exceptionFunction);
    }
}
