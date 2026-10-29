package nadiendev.ae2usefulutilities.compat.jei;

import mezz.jei.api.gui.drawable.IScalableDrawable;
import mezz.jei.common.Internal;
import mezz.jei.common.gui.textures.Textures;
import nadiendev.ae2usefulutilities.config.AE2UtilitiesClientConfig;
import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import nadiendev.ae2usefulutilities.stock.client.NetworkStockCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public final class StockToggleButton {

    private static final int SIZE = 20;

    private static final Identifier ICON =
            Identifier.fromNamespaceAndPath(PatternNetwork.MODID, "textures/gui/stock_icon.png");

    private static int x;
    private static int y;
    private static boolean visible;

    private StockToggleButton() {
    }

    public static boolean isEnabled() {
        return AE2UtilitiesClientConfig.SHOW_NETWORK_STOCK.get();
    }

    public static void beginFrame() {
        visible = false;
    }

    public static void setPosition(int left, int top) {
        x = left;
        y = top;
    }

    private static boolean isOver(double mouseX, double mouseY) {
        return visible && mouseX >= x && mouseX < x + SIZE && mouseY >= y && mouseY < y + SIZE;
    }

    private static IScalableDrawable background(boolean pressed, boolean hovered) {
        Textures textures = Internal.getTextures();
        if (pressed) {
            return hovered ? textures.getButtonPressedHighlight() : textures.getButtonPressed();
        }
        return hovered ? textures.getButtonHighlight() : textures.getButtonEnabled();
    }

    public static void draw(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        visible = true;
        background(isEnabled(), isOver(mouseX, mouseY)).draw(guiGraphics, x, y, SIZE, SIZE);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, ICON, x + 2, y + 2, 0, 0, 16, 16, 16, 16);
    }

    public static void drawTooltip(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if (!isOver(mouseX, mouseY)) {
            return;
        }
        boolean enabled = isEnabled();
        guiGraphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, List.of(
                Component.translatable("ae2usefulutilities.jei.stock.title"),
                Component.translatable(enabled
                        ? "ae2usefulutilities.jei.stock.on"
                        : "ae2usefulutilities.jei.stock.off")), mouseX, mouseY);
    }

    public static boolean handleClick(double mouseX, double mouseY, int button) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT || !isOver(mouseX, mouseY)) {
            return false;
        }
        boolean enabled = !isEnabled();
        AE2UtilitiesClientConfig.SHOW_NETWORK_STOCK.set(enabled);
        AE2UtilitiesClientConfig.SHOW_NETWORK_STOCK.save();
        if (!enabled) {
            NetworkStockCache.clear();
        }
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
        return true;
    }
}
