package me.thepond.packets;

import me.thepond.SignsOfLifeClient;
import me.thepond.data.Region;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class EditRegionS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf byteBuf, PacketSender sender)
    {
        boolean bl = byteBuf.readBoolean();
        if (bl) {
            Region region = Region.fromNbt(byteBuf.readNbt());
            client.execute(() -> {
                SignsOfLifeClient.setEditingRegion(region);
            });
        } else {
            client.execute(() -> {
                SignsOfLifeClient.setEditingRegion(null);
            });
        }
    }
}
