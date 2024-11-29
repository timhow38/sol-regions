package me.thepond.solregions.data;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class RegionDataManager {

    private static final String REGION_DATA_NAME = "region_data";

    public static RegionData getRegionData(MinecraftServer server) {
        ServerWorld world = server.getWorld(World.OVERWORLD);
        PersistentStateManager stateManager = world.getPersistentStateManager();
        return stateManager.getOrCreate(RegionData::fromNbt, RegionData::new, REGION_DATA_NAME);
    }

    public static RegionData getRegionData(ServerWorld world) {
        PersistentStateManager stateManager = world.getPersistentStateManager();
        return stateManager.getOrCreate(RegionData::fromNbt, RegionData::new, REGION_DATA_NAME);
    }

}
