package org.Rezeptesammler.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.Rezeptesammler.Model.Recipe;
import org.lightcouch.CouchDbClient;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

@Service
public class RecipeServiceImpl implements RecipeService {

    private static final CouchDbClient dbClient = new CouchDbClient("couchdb_recipe.properties");
    private static final String DOWNLOAD_DIR = "downloads";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Recipe> getAllRecipes() {
        return dbClient.view("Recipes/allRecipes").includeDocs(true).query(Recipe.class);
    }

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

                // Prompt für Ollama
                String prompt = "Extrahiere aus dem folgenden Text das Rezept. Gib das Ergebnis als JSON mit zwei Feldern zurück: " +
                        "\"ingredients\" und \"steps\".\n\nText:\n" + description;

                System.out.println("▶ Starte Ollama Anfrage...");

                ProcessBuilder ollamaPb = new ProcessBuilder(
                        "C:\\Users\\hecht\\AppData\\Local\\Programs\\Ollama\\ollama.exe",
                        "run", "llama3.2", prompt
                );
                ollamaPb.redirectErrorStream(true);
                Process ollamaProcess = ollamaPb.start();

                StringBuilder ollamaOutput = new StringBuilder();
                try (BufferedReader ollamaReader = new BufferedReader(new InputStreamReader(ollamaProcess.getInputStream()))) {
                    String line;
                    while ((line = ollamaReader.readLine()) != null) {
                        System.out.println("[Ollama] " + line); // Live-Ausgabe
                        ollamaOutput.append(line).append("\n");
                    }
                }

                int ollamaExitCode = ollamaProcess.waitFor();
                System.out.println("Ollama beendet mit Code: " + ollamaExitCode);

                if (ollamaExitCode == 0) {
                    try {
                        JsonNode recipeJson = objectMapper.readTree(ollamaOutput.toString());
                        recipe.setIngredients(objectMapper.convertValue(recipeJson.get("ingredients"), List.class));
                        recipe.setSteps(objectMapper.convertValue(recipeJson.get("steps"), List.class));
                    } catch (Exception e) {
                        System.err.println("❌ Fehler beim Parsen der Ollama-Antwort: " + e.getMessage());
                    }
                } else {
                    System.err.println("❌ Ollama Prozess fehlgeschlagen mit Code: " + ollamaExitCode);
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
}
