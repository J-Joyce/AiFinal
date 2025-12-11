package net.TimeIsWhat.AIFinal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DataLogger {

    private static final Gson GSON = new GsonBuilder().create();
    private static Path logPath;

    public static void init(Path configDir) {
        // Example path: .minecraft/config/aifinal/data.jsonl
        logPath = configDir.resolve("aifinal").resolve("data.jsonl");

        try {
            Files.createDirectories(logPath.getParent());
            if (!Files.exists(logPath)) {
                Files.createFile(logPath);
            }
        } catch (IOException e) {
            // In a real mod, you'd use a logger instead of printStackTrace
            e.printStackTrace();
        }
    }

    public static void log(JsonObject obj) {
        if (logPath == null) {
            // Not initialized; avoid NPE
            return;
        }

        try (FileWriter writer = new FileWriter(logPath.toFile(), true)) {
            writer.write(GSON.toJson(obj));
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

