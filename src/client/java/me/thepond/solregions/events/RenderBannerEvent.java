package me.thepond.solregions.events;

import com.mojang.blaze3d.systems.RenderSystem;
import me.thepond.solregions.SOLRegions;
import me.thepond.solregions.SOLRegionsConfig;
import me.thepond.solregions.TextureLoader;
import me.thepond.solregions.data.Region;
import me.thepond.solregions.screens.SOLRegionsOptionsScreen;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class RenderBannerEvent implements HudRenderCallback, ScreenEvents.AfterRender {

    private static final Identifier BANNER_TEXTURE = new Identifier(SOLRegions.MOD_ID, "textures/gui/banner.png");

    private static Region currentRegion;
    private static int regionEnterTicks = 0;

    private static final int FADE_IN_DURATION = 60; // 1 second (20 ticks)
    private static final int STAY_DURATION = 200; // 2 seconds (40 ticks)
    private static final int FADE_OUT_DURATION = 60; // 1 second (20 ticks)

    public static void enterRegion(Region region) {
        currentRegion = region;
        regionEnterTicks = FADE_IN_DURATION + STAY_DURATION + FADE_OUT_DURATION;
    }

    public static void leaveRegion() {
        currentRegion = null;
        regionEnterTicks = 0;
    }

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta) {
        if (regionEnterTicks > 0 && currentRegion != null) {
            renderBanner(drawContext, currentRegion.isShowingBannerName() ? currentRegion.getRegionName() : "", currentRegion.isShowingBannerName() ? currentRegion.getRegionDescription() : "");
            regionEnterTicks -= tickDelta;
        } else if (currentRegion != null) {
            renderCornerBanner(drawContext, currentRegion, currentRegion.getRegionName(), currentRegion.getRegionDescription(), false);
        } else {
            renderCornerBanner(drawContext, null, "Wilds", "", false);
        }
    }

    private float calculateAlpha(int regionEnterTicks) {
        if (regionEnterTicks <= 0) {
            return 0.0f; // Fully transparent when no longer in the region
        }

        int elapsedTicks = FADE_IN_DURATION + STAY_DURATION + FADE_OUT_DURATION - regionEnterTicks;

        if (elapsedTicks < FADE_IN_DURATION) {
            // Fade in
            return elapsedTicks / (float) FADE_IN_DURATION;
        } else if (elapsedTicks < FADE_IN_DURATION + STAY_DURATION) {
            // Stay
            return 1.0f;
        } else if (elapsedTicks < FADE_IN_DURATION + STAY_DURATION + FADE_OUT_DURATION) {
            // Fade out
            int fadeOutTicks = elapsedTicks - (FADE_IN_DURATION + STAY_DURATION);
            return 1.0f - (fadeOutTicks / (float) FADE_OUT_DURATION);
        }

        return 0.0f; // Fully transparent after fade-out duration
    }

    private int getTitleXPosition(DrawContext drawContext, String title) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        Window window = client.getWindow();

        float configScale = SOLRegionsOptionsScreen.getTextScale(SOLRegionsConfig.getFloat("textScale", 0.1837f));
        int textWidth = textRenderer.getWidth(title);
        float titleScale = 1.5f * configScale;

        boolean isInventoryOpen = client.currentScreen instanceof AbstractInventoryScreen<?>;

        int textPositionIndex = SOLRegionsConfig.getInt(isInventoryOpen ? "inventoryOpenedPosition" : "defaultPosition", 0);

        // 0 = Top Right, 1 = Top Left, 2 = Bottom Left, 3 = Bottom Right

        if (textPositionIndex == 0) {
            return (int) (window.getScaledWidth() - (textWidth * titleScale) - 15);
        } else if (textPositionIndex == 1) {
            return 15;
        } else if (textPositionIndex == 2) {
            return 15;
        } else if (textPositionIndex == 3) {
            return (int) (window.getScaledWidth() - (textWidth * titleScale) - 15);
        }

        return 0;
    }

    private int getTitleYPosition(DrawContext drawContext, String title) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        Window window = client.getWindow();

        float configScale = SOLRegionsOptionsScreen.getTextScale(SOLRegionsConfig.getFloat("textScale", 0.1837f));
        float titleScale = 1.5f * configScale;
        int textHeight = textRenderer.fontHeight;

        boolean isInventoryOpen = client.currentScreen instanceof AbstractInventoryScreen<?>;

        int textPositionIndex = SOLRegionsConfig.getInt(isInventoryOpen ? "inventoryOpenedPosition" : "defaultPosition", 0);

        // 0 = Top Right, 1 = Top Left, 2 = Bottom Left, 3 = Bottom Right

        if (textPositionIndex == 0) {
            return 10;
        } else if (textPositionIndex == 1) {
            return 10;
        } else if (textPositionIndex == 2) {
            return window.getScaledHeight() - 10 - (int)(textHeight * titleScale);
        } else if (textPositionIndex == 3) {
            return window.getScaledHeight() - 10 - (int)(textHeight * titleScale);
        }

        return 0;
    }

    private void renderCornerBanner(DrawContext drawContext, @Nullable Region region, String title, String description, boolean inventoryOpen) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        boolean isInventoryOpen = client.currentScreen instanceof AbstractInventoryScreen<?>;
        boolean isHidingRegionName = SOLRegionsConfig.getBoolean("hideRegionName", false);

        if (!inventoryOpen && isHidingRegionName) {
            return;
        }

        if (isInventoryOpen && !inventoryOpen) return;

        MatrixStack matrixStack = drawContext.getMatrices();
        float configScale = SOLRegionsOptionsScreen.getTextScale(SOLRegionsConfig.getFloat("textScale", 0.1837f));
        float titleScale = 1.5f * configScale;
        int titleX = getTitleXPosition(drawContext, title);
        int titleY = getTitleYPosition(drawContext, title);
        matrixStack.push();
        matrixStack.translate(titleX, titleY, 0f);
        matrixStack.scale(titleScale, titleScale, 1.0f);
        drawContext.drawTextWithShadow(textRenderer, title, 0, 0, 0xFFFFFF);
        if (region != null) RenderRegionEvent.TEXT.invoker().onText(drawContext, region);
        matrixStack.pop();
    }

    private void renderBanner(DrawContext drawContext, String title, String description) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        Window window = client.getWindow();

        // Dimensions and positions
        int bannerWidth = 256;
        int bannerHeight = 64;
        int x = (window.getScaledWidth() - bannerWidth) / 2;
        int y = window.getScaledHeight() / 6;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Bind the banner texture

        Identifier bannerTexture = TextureLoader.getTexture(currentRegion.getRegionName());
        Identifier defaultTexture = TextureLoader.getTexture("default");

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, Math.min(0.9f, calculateAlpha(regionEnterTicks)));
        // Render the banner background
        drawContext.drawTexture(bannerTexture == null ? defaultTexture == null ? BANNER_TEXTURE : defaultTexture : bannerTexture, x, y, 0, 0, bannerWidth, bannerHeight, bannerWidth, bannerHeight);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, calculateAlpha(regionEnterTicks));

        // Render the title with scaling
        MatrixStack matrixStack = drawContext.getMatrices();
        int titleX = x + (bannerWidth / 2);
        int titleY = y + 10;
        matrixStack.push();
        float titleScale = 2.0f; // Scale the title text
        matrixStack.translate(titleX, titleY, 0);
        matrixStack.scale(titleScale, titleScale, 1.0f);
        int scaledTitleX = -textRenderer.getWidth(title) / 2;
        drawContext.drawTextWithShadow(textRenderer, title, scaledTitleX, 0, 0xFFFFFF);
        matrixStack.pop();

        // Render the description without scaling
        int descX = x + (bannerWidth / 2) - (textRenderer.getWidth(description) / 2);
        int descY = y + 40; // Adjusted y position to account for title scaling
        drawContext.drawTextWithShadow(textRenderer, description, descX, descY, 0xAAAAAA);

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1f);
    }

    @Override
    public void afterRender(Screen screen, DrawContext drawContext, int mouseX, int mouseY, float tickDelta) {
        if (screen instanceof AbstractInventoryScreen<?>) {
            if (currentRegion != null) {
                renderCornerBanner(drawContext, currentRegion, currentRegion.getRegionName(), currentRegion.getRegionDescription(), true);
            } else {
                renderCornerBanner(drawContext, null, "Wilds", "", true);
            }
        }
    }
}
