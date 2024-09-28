package me.thepond.solregions.screens;

import com.terraformersmc.modmenu.config.ModMenuConfigManager;
import me.thepond.solregions.SOLRegionsConfig;
import me.thepond.solregions.TextureLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

public class SOLRegionsOptionsScreen extends GameOptionsScreen {

    private static final List<String> OPTIONS = Arrays.asList("Top Right", "Top Left", "Bottom Left", "Bottom Right");

    private Screen previous;

    private int defaultPositionIndex = 0;
    private int inventoryOpenedPositionIndex = 0;

    public SOLRegionsOptionsScreen(Screen parent) {
        super(parent, MinecraftClient.getInstance().options, Text.literal("SOL - Tribes Options"));
        this.previous = parent;
    }

    public static float getTextScale(float value) {
        return (float) Math.round((value * 4.9F + 0.1F) * 10) / 10;
    }

    @Override
    protected void init() {
        super.init();

        defaultPositionIndex = SOLRegionsConfig.getInt("defaultPosition");
        inventoryOpenedPositionIndex = SOLRegionsConfig.getInt("inventoryOpenedPosition");
        boolean hidesRegionName = SOLRegionsConfig.getBoolean("hideRegionName");

        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().marginX(5).marginBottom(4).alignHorizontalCenter();
        GridWidget.Adder adder = gridWidget.createAdder(2);

        adder.add(new SliderWidget(0, 0, 150, 20, Text.literal("Text Scale: " + getTextScale(SOLRegionsConfig.getFloat("textScale", 0.1837f))), SOLRegionsConfig.getFloat("textScale", 0.1837f)) {
            @Override
            protected void updateMessage() {
                this.setMessage(Text.of("Text Scale: " + getTextScale(SOLRegionsConfig.getFloat("textScale", 0.1837f))));
            }

            @Override
            protected void applyValue() {
                SOLRegionsConfig.setFloat("textScale", (float) this.value);
                SOLRegionsConfig.saveOptions();
            }
        });

        adder.add(ButtonWidget.builder(Text.of(hidesRegionName ? "Show Region Name" : "Hide Region Name"), button -> {
            boolean hidesRegionNameBool = SOLRegionsConfig.getBoolean("hideRegionName");
            button.setMessage(Text.of(!hidesRegionNameBool ? "Show Region Name" : "Hide Region Name"));
            SOLRegionsConfig.setBoolean("hideRegionName", !hidesRegionNameBool);
            SOLRegionsConfig.saveOptions();
        }).build());

        adder.add(ButtonWidget.builder(Text.of("Default position: " + OPTIONS.get(defaultPositionIndex)), button -> {
            defaultPositionIndex = (defaultPositionIndex + 1) % OPTIONS.size();
            button.setMessage(Text.of("Default Title Position: " + OPTIONS.get(defaultPositionIndex)));
            SOLRegionsConfig.setInt("defaultPosition", defaultPositionIndex);
            SOLRegionsConfig.saveOptions();
        }).build());

        adder.add(ButtonWidget.builder(Text.of("When inventory opened: " + OPTIONS.get(inventoryOpenedPositionIndex)), button -> {
            inventoryOpenedPositionIndex = (inventoryOpenedPositionIndex + 1) % OPTIONS.size();
            button.setMessage(Text.of("Title Position on Inventory: " + OPTIONS.get(inventoryOpenedPositionIndex)));
            SOLRegionsConfig.setInt("inventoryOpenedPosition", inventoryOpenedPositionIndex);
            SOLRegionsConfig.saveOptions();
        }).build());

        adder.add(ButtonWidget.builder(Text.of("Clear Banner Cache"), button -> {
            TextureLoader.clearCache();
        }).build());

        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, this.height / 6 - 12, this.width, this.height, 0.5F, 0.0F);
        gridWidget.forEachChild(this::addDrawableChild);

        this.addDrawableChild(
                ButtonWidget.builder(ScreenTexts.DONE, (button) -> {
                            ModMenuConfigManager.save();
                            this.client.setScreen(this.previous);
                        }).position(this.width / 2 - 100, this.height - 27)
                        .size(200, 20)
                        .build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 5, 0xffffff);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void removed() {
        SOLRegionsConfig.saveOptions();
    }
}
