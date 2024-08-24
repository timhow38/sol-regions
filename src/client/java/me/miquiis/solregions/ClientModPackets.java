package me.thepond.solregions;

import me.thepond.solregions.packets.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientModPackets {

    public static void registerS2CPackets() {
        SOLRegions.LOGGER.debug("Registering S2C packets...");
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.HIGHLIGHT_BLOCKS, HighlightBlocksS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.ENTER_REGION, EnterRegionS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.LEAVE_REGION, LeaveRegionS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.EDIT_REGION, EditRegionS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.DOWNLOAD_BANNER_TEXTURES, DownloadBannerTexturesS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.CHECK_BANNER_TEXTURES, CheckBannerTexturesS2CPacket::receive);
    }
}
