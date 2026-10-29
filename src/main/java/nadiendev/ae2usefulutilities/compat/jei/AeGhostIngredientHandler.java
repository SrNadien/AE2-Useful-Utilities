package nadiendev.ae2usefulutilities.compat.jei;

import appeng.client.gui.AEBaseScreen;
import appeng.menu.slot.FakeSlot;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("rawtypes")
public class AeGhostIngredientHandler implements IGhostIngredientHandler<AEBaseScreen> {

    @Override
    public <I> List<Target<I>> getTargetsTyped(AEBaseScreen screen, ITypedIngredient<I> ingredient, boolean doStart) {
        ItemStack dragged = JeiKeys.toFilterStack(ingredient);
        if (dragged == null) {
            return Collections.emptyList();
        }
        List<Target<I>> targets = new ArrayList<>();
        for (Object entry : screen.getMenu().slots) {
            if (entry instanceof FakeSlot slot && slot.isActive() && slot.canSetFilterTo(dragged)) {
                targets.add(new SlotTarget<>(screen, slot, dragged));
            }
        }
        return targets;
    }

    @Override
    public void onComplete() {
    }

    private static final class SlotTarget<I> implements Target<I> {
        private final FakeSlot slot;
        private final ItemStack stack;
        private final Rect2i area;

        private SlotTarget(AEBaseScreen<?> screen, Slot slot, ItemStack stack) {
            this.slot = (FakeSlot) slot;
            this.stack = stack;
            this.area = new Rect2i(screen.getGuiLeft() + slot.x, Math.max(0, screen.getGuiTop()) + slot.y, 16, 16);
        }

        @Override
        public Rect2i getArea() {
            return area;
        }

        @Override
        public void accept(I ingredient) {
            slot.setFilterTo(stack);
        }
    }
}
