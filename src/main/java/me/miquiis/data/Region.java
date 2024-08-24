package me.thepond.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Region {

    private String regionName;
    private String regionDescription;
    private List<BlockPos> regionVertices;
    private List<BlockPos> editingRegionVertices;
    private int regionMinY;
    private int regionMaxY;
    private boolean showBannerName;

    public Region() {
        this.regionName = "";
        this.regionDescription = "";
        this.regionVertices = new ArrayList<>();
        this.editingRegionVertices = new ArrayList<>();
        this.regionMinY = 0;
        this.regionMaxY = 0;
        this.showBannerName = true;
    }

    public Region(String regionName, String regionDescription) {
        this.regionName = regionName;
        this.regionDescription = regionDescription;
        this.regionMinY = 0;
        this.regionMaxY = 0;
        this.regionVertices = new ArrayList<>();
        this.editingRegionVertices = new ArrayList<>();
        this.showBannerName = true;
    }

    public Region(String regionName, String regionDescription, List<BlockPos> regionVertices, int regionMinY, int regionMaxY) {
        this.regionName = regionName;
        this.regionDescription = regionDescription;
        this.regionVertices = regionVertices;
        this.regionMinY = regionMinY;
        this.regionMaxY = regionMaxY;
        this.editingRegionVertices = new ArrayList<>();
        this.showBannerName = true;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public void setShowBannerName(boolean showBannerName) {
        this.showBannerName = showBannerName;
    }

    public void setRegionDescription(String regionDescription) {
        this.regionDescription = regionDescription;
    }

    public void setRegionVertices(List<BlockPos> regionVertices) {
        this.regionVertices = regionVertices;
    }

    public void setEditingRegionVertices(List<BlockPos> editingRegionVertices) {
        this.editingRegionVertices = editingRegionVertices;
    }

    public void setRegionMinY(int regionMinY) {
        this.regionMinY = regionMinY;
    }

    public void setRegionMaxY(int regionMaxY) {
        this.regionMaxY = regionMaxY;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getRegionDescription() {
        return regionDescription;
    }

    public List<BlockPos> getRegionVertices() {
        return regionVertices;
    }

    public boolean isShowingBannerName() {
        return showBannerName;
    }

    public List<BlockPos> getEditingRegionVertices() {
        return editingRegionVertices;
    }

    public int getRegionMinY() {
        return regionMinY;
    }

    public int getRegionMaxY() {
        return regionMaxY;
    }

    public PolygonFiller getPolygonFiller() {
        return new PolygonFiller(regionVertices, regionMinY, regionMaxY);
    }

    public List<ServerPlayerEntity> getPlayersInRegion(ServerWorld serverWorld) {
        List<ServerPlayerEntity> players = new ArrayList<>();
        for (ServerPlayerEntity player : serverWorld.getPlayers()) {
            if (getPolygonFiller().isPointInPolygon(player.getBlockPos())) {
                players.add(player);
            }
        }
        return players;
    }

    public boolean isInRegion(BlockPos pos) {
        return getPolygonFiller().isPointInPolygon(pos);
    }

    public static Region fromNbt(NbtCompound nbt) {
        Region region = new Region();
        region.regionName = nbt.getString("regionName");
        region.regionDescription = nbt.getString("regionDescription");
        region.regionMinY = nbt.getInt("regionMinY");
        region.regionMaxY = nbt.getInt("regionMaxY");
        region.showBannerName = !nbt.contains("showBannerName") || nbt.getBoolean("showBannerName");
        int regionVerticesSize = nbt.getInt("regionVerticesSize");
        for (int i = 0; i < regionVerticesSize; i++) {
            region.regionVertices.add(BlockPos.fromLong(nbt.getLong("regionVertex" + i)));
        }
        if (nbt.contains("editingRegionVerticesSize")) {
            int editingRegionVerticesSize = nbt.getInt("editingRegionVerticesSize");
            for (int i = 0; i < editingRegionVerticesSize; i++) {
                region.editingRegionVertices.add(BlockPos.fromLong(nbt.getLong("editingRegionVertex" + i)));
            }
        }
        return region;
    }

    public NbtCompound toNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("regionName", regionName);
        nbt.putString("regionDescription", regionDescription);
        nbt.putInt("regionMinY", regionMinY);
        nbt.putInt("regionMaxY", regionMaxY);
        nbt.putInt("regionVerticesSize", regionVertices.size());
        nbt.putBoolean("showBannerName", showBannerName);
        for (int i = 0; i < regionVertices.size(); i++) {
            nbt.putLong("regionVertex" + i, regionVertices.get(i).asLong());
        }
        nbt.putInt("editingRegionVerticesSize", editingRegionVertices.size());
        for (int i = 0; i < editingRegionVertices.size(); i++) {
            nbt.putLong("editingRegionVertex" + i, editingRegionVertices.get(i).asLong());
        }
        return nbt;
    }

}
