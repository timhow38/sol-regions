package me.thepond;

import me.thepond.command.ModCommands;
import me.thepond.events.RegionVisitEvent;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SignsOfLife implements ModInitializer {

	public static final String MOD_ID = "signs-of-life";
    public static final Logger LOGGER = LoggerFactory.getLogger("signs-of-life");

	@Override
	public void onInitialize() {
		ModPackets.registerC2SPackets();
		TextureManager.loadTextures();
		CommandRegistrationCallback.EVENT.register(new ModCommands());
		ServerTickEvents.END_SERVER_TICK.register(new RegionVisitEvent());
		ServerTickEvents.END_SERVER_TICK.register(new Scheduler());
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerPlayNetworking.send(handler.player, ModPackets.CHECK_BANNER_TEXTURES, TextureManager.createCheckBannerPacket());
		});
	}
}