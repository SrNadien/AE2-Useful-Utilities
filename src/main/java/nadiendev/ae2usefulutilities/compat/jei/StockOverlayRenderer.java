package nadiendev.ae2usefulutilities.compat.jei;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AmountFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.common.util.ImmutableRect2i;
import nadiendev.ae2usefulutilities.stock.client.NetworkStockCache;
import mezz.jei.gui.overlay.IngredientListSlot;
import mezz.jei.gui.overlay.elements.IElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Optional;

public final class StockOverlayRenderer {

    private static final int STORED_COLOR = 0xFFFFFF;
    private static final int CRAFTABLE_COLOR = 0x55FF55;

    private StockOverlayRenderer() {
    }

    public static void render(GuiGraphics guiGraphics, List<IngredientListSlot> slots) {
        if (!StockToggleButton.isEnabled() || !NetworkStockCache.isConnected()) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        for (IngredientListSlot slot : slots) {
            if (slot.isBlocked()) {
                continue;
            }
            Optional<IElement<?>> element = slot.getOptionalElement();
            if (element.isEmpty()) {
                continue;
            }
            AEKey key = JeiKeys.keyOf(element.get().getTypedIngredient());
            if (key == null) {
                continue;
            }
            long amount = NetworkStockCache.amount(key);
            String text;
            int color;
            if (amount > 0) {
                text = key.formatAmount(amount, AmountFormat.SLOT);
                color = STORED_COLOR;
            } else if (NetworkStockCache.isCraftable(key)) {
                text = Component.translatable("ae2usefulutilities.jei.stock.craft").getString();
                color = CRAFTABLE_COLOR;
            } else {
                continue;
            }
            ImmutableRect2i area = slot.getArea();
            drawCorner(guiGraphics, font, text, color,
                    area.getX() + slot.getPadding() + 16, area.getY() + slot.getPadding() + 16);
        }
    }

    private static void drawCorner(GuiGraphics guiGraphics, Font font, String text, int color, int right, int bottom) {
        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(right, bottom, 200);
        pose.scale(0.5F, 0.5F, 1.0F);
        guiGraphics.drawString(font, text, -font.width(text), -font.lineHeight, color, true);
        pose.popPose();
    }

}
