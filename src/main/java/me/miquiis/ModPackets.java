package me.thepond;

import me.thepond.packets.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModPackets {

    // Server to client packets.
    public static final Identifier HIGHLIGHT_BLOCKS = new Identifier(SignsOfLife.MOD_ID, "highlight_blocks");
    public static final Identifier ENTER_REGION = new Identifier(SignsOfLife.MOD_ID, "enter_region");
    public static final Identifier LEAVE_REGION = new Identifier(SignsOfLife.MOD_ID, "leave_region");
    public static final Identifier EDIT_REGION = new Identifier(SignsOfLife.MOD_ID, "edit_region");
    public static final Identifier DOWNLOAD_BANNER_TEXTURES = new Identifier(SignsOfLife.MOD_ID, "download_banner_textures");
    public static final Identifier CHECK_BANNER_TEXTURES = new Identifier(SignsOfLife.MOD_ID, "check_banner_textures");

    //Client to server packets.
    public static final Identifier ADD_VERTEX_TO_REGION = new Identifier(SignsOfLife.MOD_ID, "add_vertex_to_region");
    public static final Identifier REMOVE_VERTEX_FROM_REGION = new Identifier(SignsOfLife.MOD_ID, "remove_vertex_from_region");
    public static final Identifier UPDATE_REGION_BLOCKS = new Identifier(SignsOfLife.MOD_ID, "update_region_blocks");
    public static final Identifier REQUEST_BANNER_TEXTURES = new Identifier(SignsOfLife.MOD_ID, "request_banner_textures");
    public static final Identifier REQUEST_ALL_BANNERS_TEXTURES = new Identifier(SignsOfLife.MOD_ID, "request_all_banners_textures");

    public static void registerC2SPackets() {
        SignsOfLife.LOGGER.debug("Registering C2S packets...");
        ServerPlayNetworking.registerGlobalReceiver(ADD_VERTEX_TO_REGION, (server, player, handler, buf, responseSender) -> {
            AddVertexToRegionC2SPacket.receive(server, handler, buf, responseSender);
        });
        ServerPlayNetworking.registerGlobalReceiver(REMOVE_VERTEX_FROM_REGION, (server, player, handler, buf, responseSender) -> {
            RemoveVertexFromRegionC2SPacket.receive(server, handler, buf, responseSender);
        });
        ServerPlayNetworking.registerGlobalReceiver(UPDATE_REGION_BLOCKS, (server, player, handler, buf, responseSender) -> {
            UpdateRegionBlocksC2SPacket.receive(server, handler, buf, responseSender);
        });
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_BANNER_TEXTURES, (server, player, handler, buf, responseSender) -> {
            RequestBannerTexturesC2SPacket.receive(server, handler, buf, responseSender);
        });
        ServerPlayNetworking.registerGlobalReceiver(REQUEST_ALL_BANNERS_TEXTURES, (server, player, handler, buf, responseSender) -> {
            RequestAllBannersTexturesC2SPacket.receive(server, handler, buf, responseSender);
        });
    }

}
