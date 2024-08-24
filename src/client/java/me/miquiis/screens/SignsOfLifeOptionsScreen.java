package me.thepond.screens;

import me.thepond.SignsOfLifeConfig;
import me.thepond.TextureLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import java.util.Arrays;
import java.util.List;

public class SignsOfLifeOptionsScreen extends Screen {
    private static final List<String> OPTIONS = Arrays.asList("Top Right", "Top Left", "Bottom Left", "Bottom Right");

    private Screen parent;
    private int defaultPositionIndex = 0;
    private int inventoryOpenedPositionIndex = 0;

    private ButtonWidget defaultPositionButton;
    private ButtonWidget inventoryOpenedButton;
    private ButtonWidget clearCacheButton;

    public SignsOfLifeOptionsScreen() {
        super(Text.of("Signs of Life Options"));
    }

    public SignsOfLifeOptionsScreen(Screen screen) {
        super(Text.of("Signs of Life Options"));
        this.parent = screen;
    }

    @Override
    public void close() {
        SignsOfLifeConfig.loadOptions();
        super.close();
        if (this.client != null) {
            this.client.setScreen(this.parent);
        }
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.defaultPositionButton = ButtonWidget.builder(Text.of("Default position: " + OPTIONS.get(defaultPositionIndex)), button -> {
            defaultPositionIndex = (defaultPositionIndex + 1) % OPTIONS.size();
            button.setMessage(Text.of("Default Title Position: " + OPTIONS.get(defaultPositionIndex)));
            SignsOfLifeConfig.setInt("defaultPosition", defaultPositionIndex);
            SignsOfLifeConfig.saveOptions();
        }).dimensions(centerX - 100, centerY - 24, 200, 20).build();

        this.inventoryOpenedButton = ButtonWidget.builder(Text.of("When inventory opened: " + OPTIONS.get(inventoryOpenedPositionIndex)), button -> {
            inventoryOpenedPositionIndex = (inventoryOpenedPositionIndex + 1) % OPTIONS.size();
            button.setMessage(Text.of("Title Position on Inventory: " + OPTIONS.get(inventoryOpenedPositionIndex)));
            SignsOfLifeConfig.setInt("inventoryOpenedPosition", inventoryOpenedPositionIndex);
            SignsOfLifeConfig.saveOptions();
        }).dimensions(centerX - 100, centerY + 4, 200, 20).build();

        this.clearCacheButton = ButtonWidget.builder(Text.of("Clear Banner Cache"), button -> {
            TextureLoader.clearCache();
        }).dimensions(centerX - 100, centerY + 32, 200, 20).build();

        this.addDrawableChild(this.defaultPositionButton);
        this.addDrawableChild(this.inventoryOpenedButton);
        this.addDrawableChild(this.clearCacheButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
