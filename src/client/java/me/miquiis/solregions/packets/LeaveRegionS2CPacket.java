package me.thepond.solregions.packets;

import me.thepond.solregions.events.RenderBannerEvent;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

public class LeaveRegionS2CPacket {
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf byteBuf, PacketSender sender)
    {
        client.execute(RenderBannerEvent::leaveRegion);
    }
}
