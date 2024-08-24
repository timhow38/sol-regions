package me.thepond.solregions.packets;

import me.thepond.solregions.data.Region;
import me.thepond.solregions.data.RegionData;
import me.thepond.solregions.data.RegionDataManager;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class UpdateRegionBlocksC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        String regionName = buf.readString();
        server.execute(() -> {
            RegionData regionData = RegionDataManager.getRegionData(server);
            Region region = regionData.getRegion(regionName);
            if (region != null) {
                ServerWorld world = server.getWorld(World.OVERWORLD);
                region.getRegionVertices().forEach(pos -> {
                    world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                });
                region.getEditingRegionVertices().forEach(pos -> {
                    world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                });
            }
        });
    }

}
