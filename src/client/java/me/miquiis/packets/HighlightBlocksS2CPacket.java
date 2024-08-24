package me.thepond.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class HighlightBlocksS2CPacket {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf byteBuf, PacketSender sender)
    {
        List<BlockPos> blockPositions = byteBuf.readCollection(ArrayList::new, PacketByteBuf::readBlockPos);
        client.execute(() -> {
            if (client.player != null && client.player.getWorld() != null)
            {
                for (BlockPos blockPosition : blockPositions) {
                    client.player.getWorld().setBlockState(blockPosition, Blocks.GOLD_BLOCK.getDefaultState());
                }
            }
        });
    }

}
