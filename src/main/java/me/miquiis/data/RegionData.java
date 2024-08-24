package me.thepond.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.List;

public class RegionData extends PersistentState {

    private final List<Region> regions;

    public RegionData() {
        this.regions = new ArrayList<>();
    }

    public void addOrUpdateRegion(Region region) {
        regions.removeIf(r -> r.getRegionName().equals(region.getRegionName()));
        regions.add(region);
        setDirty(true);
    }

    public void removeRegion(String regionName) {
        regions.removeIf(r -> r.getRegionName().equals(regionName));
        setDirty(true);
    }

    public Region getRegion(String regionName) {
        for (Region region : regions) {
            if (region.getRegionName().equals(regionName)) {
                return region;
            }
        }
        return null;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public static RegionData fromNbt(NbtCompound nbt) {
        RegionData regionData = new RegionData();
        NbtList regionList = nbt.getList("sol_regions", 10);
        for (int i = 0; i < regionList.size(); i++) {
            regionData.regions.add(Region.fromNbt(regionList.getCompound(i)));
        }
        return regionData;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList regionList = new NbtList();
        for (Region region : regions) {
            regionList.add(region.toNbt());
        }
        nbt.put("sol_regions", regionList);
        return nbt;
    }
}
