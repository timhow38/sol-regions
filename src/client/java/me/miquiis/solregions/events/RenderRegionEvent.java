package me.thepond.solregions.events;

import me.thepond.solregions.data.Region;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.DrawContext;

public class RenderRegionEvent {

    public static final Event<RenderRegionEvent.Text> TEXT = EventFactory.createArrayBacked(RenderRegionEvent.Text.class,
            (listeners) -> (drawContext, region) -> {
                for (RenderRegionEvent.Text listener : listeners) {
                    listener.onText(drawContext, region);
                }
            });

    @FunctionalInterface
    public interface Text {
        void onText(DrawContext drawContext, Region region);
    }
}
