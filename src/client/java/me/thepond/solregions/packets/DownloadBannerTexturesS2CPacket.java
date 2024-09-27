package me.thepond.solregions.packets;

import me.thepond.solregions.TextureLoader;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class DownloadBannerTexturesS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf byteBuf, PacketSender sender)
    {
        int count = byteBuf.readInt();
        for (int i = 0; i < count; i++) {
            String fileName = byteBuf.readString();
            int length = byteBuf.readInt();
            byte[] data = new byte[length];
            byteBuf.readBytes(data);
            client.execute(() -> {
                TextureLoader.loadAndSaveTexture(fileName, data);
            });
        }
    }

}
