package me.thepond.packets;

import me.thepond.ModPackets;
import me.thepond.TextureManager;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

import java.util.HashMap;

public class RequestBannerTexturesC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayNetworkHandler handler, PacketByteBuf byteBuf, PacketSender responseSender) {
        // Receive not found textures over the client side
        HashMap<String, byte[]> textures = new HashMap<>();
        int count = byteBuf.readInt();
        for (int i = 0; i < count; i++) {
            String fileName = byteBuf.readString();
            if (TextureManager.hasTexture(fileName)) {
                textures.put(fileName, TextureManager.getTexture(fileName));
            }
        }
        // Creating and send download packet
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(textures.size());
        textures.forEach((fileName, data) -> {
            buf.writeString(fileName);
            buf.writeInt(data.length);
            buf.writeBytes(data);
        });
        server.execute(() -> {
            ServerPlayNetworking.send(handler.player, ModPackets.DOWNLOAD_BANNER_TEXTURES, buf);
        });
    }

}
