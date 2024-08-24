package me.thepond.packets;

import me.thepond.data.Region;
import me.thepond.events.RenderBannerEvent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class EnterRegionS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf byteBuf, PacketSender sender)
    {
        Region region = Region.fromNbt(byteBuf.readNbt());
        client.execute(() -> {
            RenderBannerEvent.enterRegion(region);
        });
    }
}
