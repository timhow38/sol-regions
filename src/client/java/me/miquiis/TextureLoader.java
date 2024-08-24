package me.thepond;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TextureLoader {

    public static final Path BANNERS_FOLDER = FabricLoader.getInstance().getGameDir().resolve("cached_banners/");
    private static final Map<String, Identifier> textureMap = new HashMap<>();
    private static final Map<String, String> textureHashes = new HashMap<>();

    public static void loadTextures() {
        File textureFolder = BANNERS_FOLDER.toFile();

        if (!textureFolder.exists()) {
            if (!textureFolder.mkdirs()) return;
        }

        if (!textureFolder.isDirectory()) return;

        File[] files = textureFolder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".png")) {
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    NativeImage nativeImage = NativeImage.read(inputStream);
                    NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);
                    String name = file.getName().toLowerCase().replaceAll(" ", "_");
                    Identifier identifier = new Identifier(SignsOfLife.MOD_ID, "banners/" + name);
                    MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, texture);
                    textureMap.put(name.replace(".png", ""), identifier);
                    textureHashes.put(name.replace(".png", ""), DigestUtils.sha256Hex(Files.readAllBytes(file.toPath())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void loadTexture(String name, byte[] bytes) {
        try {
            NativeImage nativeImage = NativeImage.read(bytes);
            NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);
            Identifier identifier = new Identifier(SignsOfLife.MOD_ID, "banners/" + name);
            MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, texture);
            textureMap.put(name, identifier);
            textureHashes.put(name, DigestUtils.sha256Hex(bytes));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadAndSaveTexture(String name, byte[] bytes) {
        try {
            NativeImage nativeImage = NativeImage.read(bytes);
            NativeImageBackedTexture texture = new NativeImageBackedTexture(nativeImage);
            Identifier identifier = new Identifier(SignsOfLife.MOD_ID, "banners/" + name + ".png");
            MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, texture);
            textureMap.put(name, identifier);
            textureHashes.put(name, DigestUtils.sha256Hex(bytes));

            Path texturePath = BANNERS_FOLDER.resolve(name + ".png");
            Files.write(texturePath, bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean hasTexture(String name, String hash) {
        Identifier identifier = getTexture(name);
        String textureHash = getTextureHash(name);
        System.out.println("Checking texture: " + name + " with hash: " + hash + " against: " + textureHash);
        return identifier != null && hash.equals(textureHash);
    }

    public static String getTextureHash(String name) {
        return textureHashes.get(name);
    }

    public static String getTextureHash(Identifier textureId) {
        MinecraftClient client = MinecraftClient.getInstance();
        NativeImageBackedTexture texture = (NativeImageBackedTexture) client.getTextureManager().getTexture(textureId);

        if (texture == null) {
            return "Texture not found";
        }

        try {
            Path tempFile = Files.createTempFile("tempTexture", ".png");
            NativeImage nativeImage = texture.getImage();

            nativeImage.writeTo(tempFile);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (var inputStream = Files.newInputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }
            }

            // Compute SHA-256 hash
            byte[] imageBytes = baos.toByteArray();
            String hash = DigestUtils.sha256Hex(imageBytes);

            Files.deleteIfExists(tempFile);

            return hash;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error computing hash";
        }
    }

    public static Identifier getTexture(String name) {
        String formattedName = name.toLowerCase().replaceAll(" ", "_");
        return textureMap.get(formattedName);
    }

    public static void clearCache() {
        textureMap.clear();
        textureHashes.clear();

        File textureFolder = BANNERS_FOLDER.toFile();

        if (!textureFolder.exists()) {
            if (!textureFolder.mkdirs()) return;
        }

        if (!textureFolder.isDirectory()) return;

        File[] files = textureFolder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".png")) {
                file.delete();
            }
        }

        if (MinecraftClient.getInstance().player != null) {
            ClientPlayNetworking.send(ModPackets.REQUEST_ALL_BANNERS_TEXTURES, PacketByteBufs.create());
        }
    }
}
