package me.thepond;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.PacketByteBuf;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextureManager {
    public static final Path BANNERS_FOLDER = FabricLoader.getInstance().getGameDir().resolve("banners/");
    private static final Map<String, byte[]> textureMap = new HashMap<>();
    private static PacketByteBuf cachedPacketByteBuf;

    public static void loadTextures() {
        cachedPacketByteBuf = null;
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
                try {
                    byte[] data = Files.readAllBytes(file.toPath());
                    textureMap.put(file.getName().toLowerCase().replaceAll(" ", "_").replace(".png", ""), data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static PacketByteBuf createCheckBannerPacket() {
        if (cachedPacketByteBuf != null) return cachedPacketByteBuf;
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(textureMap.size());
        textureMap.forEach((name, data) -> {
            buf.writeString(name);
            buf.writeString(DigestUtils.sha256Hex(data));
        });
        cachedPacketByteBuf = buf;
        return buf;
    }

    public static List<String> getTextureNames() {
        return new ArrayList<>(textureMap.keySet());
    }

    public static boolean hasTexture(String name, String hash) {
        return textureMap.containsKey(name) && DigestUtils.sha256Hex(textureMap.get(name)).equals(hash);
    }

    public static boolean hasTexture(String name) {
        return textureMap.containsKey(name);
    }

    public static byte[] getTexture(String name) {
        return textureMap.get(name);
    }

    public static String getTextureHash(String name) {
        return DigestUtils.sha256Hex(textureMap.get(name));
    }
}
