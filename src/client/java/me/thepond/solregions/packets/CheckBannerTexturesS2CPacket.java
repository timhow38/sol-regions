package me.thepond.solregions.packets;

import me.thepond.solregions.ModPackets;
import me.thepond.solregions.TextureLoader;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;

public class CheckBannerTexturesS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf byteBuf, PacketSender sender)
    {
        List<String> missingTextures = new ArrayList<>();
        int count = byteBuf.readInt();
        for (int i = 0; i < count; i++) {
            String fileName = byteBuf.readString();
            String hash = byteBuf.readString();
            if (!TextureLoader.hasTexture(fileName, hash)) {
                missingTextures.add(fileName);
            }
        }

        if (!missingTextures.isEmpty()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(missingTextures.size());
            missingTextures.forEach(buf::writeString);
            client.execute(() -> {
                ClientPlayNetworking.send(ModPackets.REQUEST_BANNER_TEXTURES, buf);
            });
        }
    }

}
