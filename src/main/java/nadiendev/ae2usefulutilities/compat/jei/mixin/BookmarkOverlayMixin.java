package nadiendev.ae2usefulutilities.compat.jei.mixin;

import mezz.jei.api.gui.handlers.IGuiProperties;
import mezz.jei.gui.elements.IconButton;
import mezz.jei.gui.overlay.bookmarks.BookmarkOverlay;
import nadiendev.ae2usefulutilities.compat.jei.StockToggleButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BookmarkOverlay.class, remap = false)
public abstract class BookmarkOverlayMixin {

    @Shadow
    @Final
    private IconButton historyButton;

    @Inject(method = "updateBounds", at = @At("TAIL"))
    private void ae2uu$placeStockButton(IGuiProperties guiProperties, CallbackInfo ci) {
        StockToggleButton.setPosition(this.historyButton.getX() + this.historyButton.getWidth() + 2,
                this.historyButton.getY());
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void ae2uu$beginStockButtonFrame(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY,
            float partialTicks, CallbackInfo ci) {
        StockToggleButton.beginFrame();
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE",
            target = "Lmezz/jei/gui/elements/IconButton;draw(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            ordinal = 1, shift = At.Shift.AFTER))
    private void ae2uu$drawStockButton(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY,
            float partialTicks, CallbackInfo ci) {
        StockToggleButton.draw(guiGraphics, mouseX, mouseY);
    }

    @Inject(method = "drawTooltips", at = @At(value = "INVOKE",
            target = "Lmezz/jei/gui/elements/IconButton;drawTooltips(Lnet/minecraft/client/gui/GuiGraphics;II)V",
            ordinal = 1, shift = At.Shift.AFTER))
    private void ae2uu$drawStockButtonTooltip(Minecraft minecraft, GuiGraphics guiGraphics, int mouseX, int mouseY,
            CallbackInfo ci) {
        StockToggleButton.drawTooltip(guiGraphics, mouseX, mouseY);
    }
}
