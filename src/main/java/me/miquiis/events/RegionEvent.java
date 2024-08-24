package me.thepond.events;

import me.thepond.data.Region;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public final class RegionEvent {

    public static final Event<Enter> ENTER_REGION = EventFactory.createArrayBacked(Enter.class,
            (listeners) -> (server, player, region) -> {
                for (Enter listener : listeners) {
                    listener.onEnter(server, player, region);
                }
            });

    public static final Event<Leave> LEAVE_REGION = EventFactory.createArrayBacked(Leave.class,
            (listeners) -> (server, player, region) -> {
                for (Leave listener : listeners) {
                    listener.onLeave(server, player, region);
                }
            });

    @FunctionalInterface
    public interface Enter {
        void onEnter(MinecraftServer server, ServerPlayerEntity player, Region region);
    }

    @FunctionalInterface
    public interface Leave {
        void onLeave(MinecraftServer server, ServerPlayerEntity player, Region region);
    }
}
