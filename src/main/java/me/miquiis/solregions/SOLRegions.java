package me.thepond.solregions;

import me.thepond.solregions.command.ModCommands;
import me.thepond.solregions.events.RegionVisitEvent;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SOLRegions implements ModInitializer {

	public static final String MOD_ID = "solregions";
    public static final Logger LOGGER = LoggerFactory.getLogger("solregions");

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