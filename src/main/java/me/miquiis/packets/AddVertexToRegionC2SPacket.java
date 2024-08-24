package me.thepond.packets;

import me.thepond.Scheduler;
import me.thepond.data.Region;
import me.thepond.data.RegionData;
import me.thepond.data.RegionDataManager;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AddVertexToRegionC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        String regionName = buf.readString();
        BlockPos pos = buf.readBlockPos();
        server.execute(() -> {
           RegionData regionData = RegionDataManager.getRegionData(server);
           Region region = regionData.getRegion(regionName);
           if (region != null) {
               if (!region.getEditingRegionVertices().contains(pos))
               {
                   region.getEditingRegionVertices().add(pos);
                   ServerWorld world = server.getWorld(World.OVERWORLD);
                   world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
               }
           }
           regionData.setDirty(true);
       });
    }
}
