package me.thepond.data;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class PolygonFiller {
    private List<BlockPos> vertices;
    private int minY;
    private int maxY;

    public PolygonFiller(List<BlockPos> vertices, int minY, int maxY) {
        this.vertices = vertices;
        this.minY = minY;
        this.maxY = maxY;
    }

    public List<BlockPos> fillPolygon() {
        List<BlockPos> positions = new ArrayList<>();

        // Calculate bounding box
        int minX = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        for (BlockPos vertex : vertices) {
            minX = Math.min(minX, vertex.getX());
            minZ = Math.min(minZ, vertex.getZ());
            maxX = Math.max(maxX, vertex.getX());
            maxZ = Math.max(maxZ, vertex.getZ());
        }

        // Iterate through bounding box including Y range
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (isPointInPolygon(pos)) {
                        positions.add(pos);
                    }
                }
            }
        }

        return positions;
    }

    public boolean isPointInPolygon(BlockPos point) {
        // Check if the point is within the Y bounds
        if (point.getY() < minY || point.getY() > maxY) {
            return false;
        }

        // Check if the point is within the polygon in the x-z plane
        boolean result = false;
        int j = vertices.size() - 1;
        for (int i = 0; i < vertices.size(); i++) {
            BlockPos vi = vertices.get(i);
            BlockPos vj = vertices.get(j);

            if ((vi.getZ() > point.getZ()) != (vj.getZ() > point.getZ()) &&
                    (point.getX() < (vj.getX() - vi.getX()) * (point.getZ() - vi.getZ()) / (vj.getZ() - vi.getZ()) + vi.getX())) {
                result = !result;
            }
            j = i;
        }
        return result;
    }
}
