package me.thepond.solregions;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.thepond.solregions.data.Region;
import me.thepond.solregions.events.RenderBannerEvent;
import me.thepond.solregions.screens.SOLRegionsOptionsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class SOLRegionsClient implements ClientModInitializer, ModMenuApi {

	private static Region editingRegion = null;

	public static Region getEditingRegion() {
		return editingRegion;
	}

	public static void setEditingRegion(Region region) {
		editingRegion = region;
	}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return SOLRegionsOptionsScreen::new;
	}

	@Override
	public void onInitializeClient() {
		SOLRegionsConfig.loadOptions();
		ClientModPackets.registerS2CPackets();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			private static final Identifier ID = new Identifier(SOLRegions.MOD_ID, "texture_reload_listener");
			@Override
			public Identifier getFabricId() {
				return ID;
			}

			@Override
			public void reload(ResourceManager manager) {
				TextureLoader.loadTextures();
			}
		});
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (world.isClient) {
				ItemStack itemStack = player.getStackInHand(hand);
				if (itemStack.getItem().equals(Items.GOLDEN_AXE)) {
					if (itemStack.hasNbt()) {
						NbtCompound nbt = itemStack.getNbt();
						if (nbt.contains("region")) {
							String regionName = nbt.getString("region");
							BlockPos pos = hitResult.getBlockPos();
							if (player.isSneaking()) {
								SOLRegions.LOGGER.info("Removing vertex at: " + pos + " for region: " + regionName);
								editingRegion.getEditingRegionVertices().remove(pos);
								PacketByteBuf buf = PacketByteBufs.create();
								buf.writeString(regionName);
								buf.writeBlockPos(pos);
								ClientPlayNetworking.send(ModPackets.REMOVE_VERTEX_FROM_REGION, buf);
							} else {
								SOLRegions.LOGGER.info("Adding vertex at: " + pos + " for region: " + regionName);
								editingRegion.getEditingRegionVertices().add(pos);
								PacketByteBuf buf = PacketByteBufs.create();
								buf.writeString(regionName);
								buf.writeBlockPos(pos);
								ClientPlayNetworking.send(ModPackets.ADD_VERTEX_TO_REGION, buf);
							}
							return ActionResult.CONSUME;
						}
					}
				}
				return ActionResult.PASS;
			}
			return ActionResult.PASS;
		});
		ClientTickEvents.START_WORLD_TICK.register(world -> {
			if (editingRegion != null) {
				editingRegion.getEditingRegionVertices().forEach(pos -> {
					world.setBlockState(pos, MinecraftClient.getInstance().player.isSneaking() ? Blocks.DIAMOND_BLOCK.getDefaultState() :  Blocks.GOLD_BLOCK.getDefaultState());
				});
			}
		});
		HudRenderCallback.EVENT.register(new RenderBannerEvent());
		ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof AbstractInventoryScreen<?>) {
				ScreenEvents.afterRender(screen).register(new RenderBannerEvent());
			}
		});
	}
}