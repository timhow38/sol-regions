package me.thepond;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SignsOfLifeConfig {

    public static final Gson GSON = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    public static final Path CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("signs-of-life.json");
    public static JsonObject CONFIG_OBJECT = new JsonObject();

    public static float getFloat(String option) {
        return getFloat(option, 1f);
    }

    public static float getFloat(String option, float defaultValue) {
        if (CONFIG_OBJECT == null || !CONFIG_OBJECT.has(option)) return defaultValue;
        return CONFIG_OBJECT.get(option).getAsFloat();
    }

    public static void setFloat(String option, float value) {
        CONFIG_OBJECT.addProperty(option, value);
    }

    public static boolean getBoolean(String option) {
        return getBoolean(option, false);
    }

    public static boolean getBoolean(String option, boolean defaultValue) {
        if (CONFIG_OBJECT == null || !CONFIG_OBJECT.has(option)) return defaultValue;
        return CONFIG_OBJECT.get(option).getAsBoolean();
    }

    public static void setBoolean(String option, boolean value) {
        CONFIG_OBJECT.addProperty(option, value);
    }

    public static int getInt(String option) {
        return getInt(option, 0);
    }

    public static int getInt(String option, int defaultValue) {
        if (CONFIG_OBJECT == null || !CONFIG_OBJECT.has(option)) return defaultValue;
        return CONFIG_OBJECT.get(option).getAsInt();
    }

    public static void setInt(String option, int value) {
        CONFIG_OBJECT.addProperty(option, value);
    }

    public static String getString(String option) {
        return getString(option, "");
    }

    public static String getString(String option, String defaultValue) {
        if (CONFIG_OBJECT == null || !CONFIG_OBJECT.has(option)) return defaultValue;
        return CONFIG_OBJECT.get(option).getAsString();
    }

    public static void setString(String option, String value) {
        CONFIG_OBJECT.addProperty(option, value);
    }

    public static void loadOptions() {
        try {
            if (CONFIG_FILE.toFile().exists())
                CONFIG_OBJECT = GSON.fromJson(Files.readString(CONFIG_FILE, StandardCharsets.UTF_8), JsonObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveOptions() {
        try {
            Files.writeString(CONFIG_FILE, GSON.toJson(CONFIG_OBJECT), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
