package me.thepond.solregions.events;

import me.thepond.solregions.ModPackets;
import me.thepond.solregions.data.Region;
import me.thepond.solregions.data.RegionData;
import me.thepond.solregions.data.RegionDataManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RegionVisitEvent implements ServerTickEvents.EndTick {

    private RegionData regionData;
    private final Map<UUID, String> playerRegionMap = new HashMap<>();

    @Override
    public void onEndTick(MinecraftServer server) {
        if (regionData == null) {
            regionData = RegionDataManager.getRegionData(server);
        }
        if (server.getTicks() % 20 == 0) {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (playerRegionMap.containsKey(player.getUuid())) {
                    // Already in a region, check if still in the same region
                    String currentRegion = playerRegionMap.get(player.getUuid());
                    Region region = regionData.getRegion(currentRegion);
                    if (region != null) {
                        if (!region.isInRegion(player.getBlockPos())) {
                            removePlayerFromRegion(player, region);
                        }
                    }
                } else {
                    // Not in a region, check if in a region
                    for (Region region : regionData.getRegions()) {
                        if (region.isInRegion(player.getBlockPos())) {
                            addPlayerToRegion(player, region);
                        }
                    }
                }
            }
        }
    }

    private void addPlayerToRegion(ServerPlayerEntity player, Region region) {
        playerRegionMap.put(player.getUuid(), region.getRegionName());
        RegionEvent.ENTER_REGION.invoker().onEnter(player.getServer(), player, region);
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeNbt(region.toNbt());
        ServerPlayNetworking.send(player, ModPackets.ENTER_REGION, buf);
    }

    private void removePlayerFromRegion(ServerPlayerEntity player, Region region) {
        playerRegionMap.remove(player.getUuid());
        RegionEvent.LEAVE_REGION.invoker().onLeave(player.getServer(), player, region);
        PacketByteBuf buf = PacketByteBufs.create();
        ServerPlayNetworking.send(player, ModPackets.LEAVE_REGION, buf);
    }
}
