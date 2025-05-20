package org.Rezeptesammler;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class OllamaHttpJsonExample {
    public static void main(String[] args) throws Exception {
        // JSON Request Body als String (du kannst es natürlich schöner mit Gson bauen)
        String requestBody = """
            {
              "model": "llama3.2",
              "messages": [{"role": "user", "content": "Tell me about Canada."}],
              "stream": false,
              "format": {
                "type": "object",
                "properties": {
                  "name": { "type": "string" },
                  "capital": { "type": "string" },
                  "languages": { "type": "array", "items": { "type": "string" } }
                },
                "required": ["name", "capital", "languages"]
              }
            }
            """;

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/api/chat"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Request senden und Antwort als String erhalten
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            System.out.println("Response JSON: " + responseBody);

            Gson gson = new Gson();
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);

            // Direkt message abrufen (kein choices Array)
            JsonObject message = json.getAsJsonObject("message");

            String contentJsonString = message.get("content").getAsString();
            System.out.println("Content JSON string: " + contentJsonString);

            // contentJsonString ist ein JSON-String, den wir nochmal parsen
            JsonObject contentJson = gson.fromJson(contentJsonString, JsonObject.class);

            System.out.println("Name: " + contentJson.get("name").getAsString());
            System.out.println("Capital: " + contentJson.get("capital").getAsString());
            System.out.println("Languages: " + contentJson.getAsJsonArray("languages").toString());

        } else {
            System.err.println("Request fehlgeschlagen mit Status: " + response.statusCode());
            System.err.println(response.body());
        }

    }
}
