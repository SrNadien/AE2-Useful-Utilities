package nadiendev.ae2usefulutilities.stock.client;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.StackWithBounds;
import nadiendev.ae2usefulutilities.compat.jei.JeiHover;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

public final class HoverKeys {

    private HoverKeys() {
    }

    @Nullable
    public static AEKey under(Screen screen, double mouseX, double mouseY) {
        if (ModList.get().isLoaded("jei")) {
            AEKey jei = JeiHover.key();
            if (jei != null) {
                return jei;
            }
        }
        if (screen instanceof AEBaseScreen<?> aeScreen) {
            StackWithBounds hovered = aeScreen.getStackUnderMouse(mouseX, mouseY);
            if (hovered != null) {
                return hovered.stack().what();
            }
        }
        if (screen instanceof AbstractContainerScreen<?> container) {
            Slot slot = container.getHoveredSlot();
            if (slot != null && slot.hasItem()) {
                GenericStack stack = GenericStack.fromItemStack(slot.getItem());
                return stack == null ? null : stack.what();
            }
        }
        return null;
    }
}
