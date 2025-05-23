package org.Rezeptesammler.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.Rezeptesammler.Model.Recipe;
import org.lightcouch.CouchDbClient;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

@Service
public class RecipeServiceImpl implements RecipeService {

    private static final CouchDbClient dbClient = new CouchDbClient("couchdb_recipe.properties");
    private static final String DOWNLOAD_DIR = "downloads";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void saveRecipe(Recipe recipe) throws IOException, InterruptedException {
        File dir = new File(DOWNLOAD_DIR);
        if (!dir.exists()) dir.mkdirs();

        recipe.set_id(UUID.randomUUID().toString());
        String baseFilename = recipe.get_id();

        // yt-dlp Download
        System.out.println("▶ Starte yt-dlp Download...");
        ProcessBuilder ytDlpPb = new ProcessBuilder(
                "yt-dlp",
                "-P", DOWNLOAD_DIR,
                "--write-info-json",
                "--write-thumbnail",
                "-o", baseFilename,
                recipe.getLink()
        );
        ytDlpPb.redirectErrorStream(true);
        Process ytDlpProcess = ytDlpPb.start();

        try (BufferedReader ytOut = new BufferedReader(new InputStreamReader(ytDlpProcess.getInputStream()))) {
            String line;
            while ((line = ytOut.readLine()) != null) {
                System.out.println("[yt-dlp] " + line);
            }
        }

        int ytExitCode = ytDlpProcess.waitFor();
        System.out.println("yt-dlp beendet mit Code: " + ytExitCode);

        if (ytExitCode != 0) {
            throw new RuntimeException("yt-dlp fehlgeschlagen mit Code: " + ytExitCode);
        }

        // Beschreibung aus JSON lesen
        File jsonFile = new File(DOWNLOAD_DIR, baseFilename + ".info.json");

        if (jsonFile.exists()) {
            JsonNode root = objectMapper.readTree(jsonFile);
            String description = root.path("description").asText(null);
            if (description != null) {
                recipe.setCaption(description);

                // Prompt aus Datei lesen
                String promptTemplate;
                try (BufferedReader br = new BufferedReader(new FileReader("src/main/java/org/Rezeptesammler/Service/prompt.txt"))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    promptTemplate = sb.toString();
                }

                // --- NEU: Audio extrahieren mit ffmpeg ---
                File mp4File = new File(DOWNLOAD_DIR, baseFilename + ".mp4");
                File wavFile = new File(DOWNLOAD_DIR, baseFilename + ".wav");

                ProcessBuilder ffmpegPb = new ProcessBuilder(
                        "ffmpeg", "-i", mp4File.getAbsolutePath(),
                        "-vn", "-acodec", "pcm_s16le", "-ar", "16000", "-ac", "1",
                        wavFile.getAbsolutePath()
                );
                ffmpegPb.redirectErrorStream(true);
                Process ffmpegProcess = ffmpegPb.start();
                try (BufferedReader ffmpegOut = new BufferedReader(new InputStreamReader(ffmpegProcess.getInputStream()))) {
                    String line;
                    while ((line = ffmpegOut.readLine()) != null) {
                        System.out.println("[ffmpeg] " + line);
                    }
                }
                int ffmpegExit = ffmpegProcess.waitFor();
                if (ffmpegExit != 0) {
                    System.err.println("❌ ffmpeg Fehler, Exit Code: " + ffmpegExit);
                    throw new RuntimeException("ffmpeg Fehler beim Audio extrahieren");
                }

                // --- NEU: Whisper Transkription ---
                ProcessBuilder whisperPb = new ProcessBuilder(
                        "whisper",
                        wavFile.getAbsolutePath(),
                        "--model", "base",
                        "--language", "de",
                        "--output_dir", wavFile.getParent(),
                        "--output_format", "txt"
                );
                whisperPb.redirectErrorStream(true);
                Process whisperProcess = whisperPb.start();
                try (BufferedReader whisperOut = new BufferedReader(new InputStreamReader(whisperProcess.getInputStream()))) {
                    String line;
                    while ((line = whisperOut.readLine()) != null) {
                        System.out.println("[whisper] " + line);
                    }
                }
                int whisperExit = whisperProcess.waitFor();
                if (whisperExit != 0) {
                    System.err.println("❌ Whisper Fehler, Exit Code: " + whisperExit);
                    throw new RuntimeException("Whisper Fehler bei Transkription");
                }

                // Transkript laden
                File txtFile = new File(DOWNLOAD_DIR, baseFilename + ".txt");
                StringBuilder transcriptBuilder = new StringBuilder();
                if (txtFile.exists()) {
                    try (BufferedReader reader = new BufferedReader(new FileReader(txtFile))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            transcriptBuilder.append(line).append("\n");
                        }
                    }
                }
                String transcription = transcriptBuilder.toString().trim();

                // Prompt zusammenbauen
                String prompt = promptTemplate
                        + "\n\n---\n\n"
                        + "🔹 Beschreibung aus Video-Metadaten:\n" + description
                        + "\n\n🔹 Transkription des Videos:\n" + transcription;

                System.out.println("▶ Sende Anfrage an Ollama HTTP API...");

                HttpClient client = HttpClient.newHttpClient();

                // JSON Request Body für Ollama HTTP API
                ObjectNode requestJson = objectMapper.createObjectNode();
                requestJson.put("model", "gemma:7b-instruct");
                requestJson.put("stream", false);

                // message
                ArrayNode messages = objectMapper.createArrayNode();
                ObjectNode userMessage = objectMapper.createObjectNode();
                userMessage.put("role", "user");
                userMessage.put("content", prompt);
                messages.add(userMessage);
                requestJson.set("messages", messages);

                // format schema
                ObjectNode format = objectMapper.createObjectNode();
                format.put("type", "object");

                ObjectNode properties = objectMapper.createObjectNode();

                // ingredients
                ObjectNode ingredientsProp = objectMapper.createObjectNode();
                ingredientsProp.put("type", "array");
                ingredientsProp.set("items", objectMapper.createObjectNode().put("type", "string"));
                properties.set("ingredients", ingredientsProp);

                // steps
                ObjectNode stepsProp = objectMapper.createObjectNode();
                stepsProp.put("type", "array");
                stepsProp.set("items", objectMapper.createObjectNode().put("type", "string"));
                properties.set("steps", stepsProp);

                // summary
                ObjectNode summaryProp = objectMapper.createObjectNode();
                summaryProp.put("type", "string");
                properties.set("summary", summaryProp);

                // category
                ObjectNode categoryProp = objectMapper.createObjectNode();
                categoryProp.put("type", "string");
                properties.set("category", categoryProp);

                format.set("properties", properties);

                ArrayNode required = objectMapper.createArrayNode();
                required.add("ingredients");
                required.add("steps");
                required.add("summary");
                required.add("category");
                format.set("required", required);

                requestJson.set("format", format);

                // HTTP Request senden
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:11434/api/chat"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestJson.toString()))
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    String responseBody = removeControlChars(response.body());
                    System.out.println("✔ Ollama Response: " + responseBody);

                    try {
                        JsonNode ollamaJson = objectMapper.readTree(responseBody);
                        String content = ollamaJson.get("message").get("content").asText();

                        JsonNode recipeJson = objectMapper.readTree(content);
                        recipe.setIngredients(objectMapper.convertValue(recipeJson.get("ingredients"), List.class));
                        recipe.setSteps(objectMapper.convertValue(recipeJson.get("steps"), List.class));
                        recipe.setSummary(recipeJson.get("summary").asText());
                        recipe.setCategory(recipeJson.get("category").asText());
                    } catch (Exception e) {
                        System.err.println("❌ Fehler beim Parsen der Ollama-Antwort: " + e.getMessage());
                    }

                } else {
                    System.err.println("❌ Ollama API fehlgeschlagen mit Status: " + response.statusCode());
                    System.err.println(response.body());
                }
            }
        }

        // Thumbnail setzen, falls vorhanden
        File thumbnail = new File(DOWNLOAD_DIR, baseFilename + ".jpg");
        if (thumbnail.exists()) {
            recipe.setThumbnail("/downloads/" + baseFilename + ".jpg");
        }

        // Speichern in CouchDB
        dbClient.save(recipe);
        System.out.println("✅ Rezept gespeichert: " + recipe.get_id());
    }

    @Override
    public boolean delteRecipe(String id) {
        // Lösche Dateien mit gleicher Basis-ID
        String[] extensions = {".info.json", ".mp4", ".jpg", ".wav", ".txt"};
        for (String ext : extensions) {
            File file = new File(DOWNLOAD_DIR, id + ext);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("🗑 Datei gelöscht: " + file.getName());
                } else {
                    System.err.println("❌ Fehler beim Löschen von Datei: " + file.getName());
                }
            }
        }
        try {
            dbClient.remove(dbClient.find(Recipe.class,id));
            System.out.println("🗑 Rezept aus Datenbank gelöscht: " + id);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Fehler beim Löschen des Rezepts aus der Datenbank: " + e.getMessage());
            return false;
        }
    }

    public void updateRecipe(Recipe recipe){
        dbClient.update(recipe);
    }

    private static String removeControlChars(String input) {
        return input.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
    }
}
