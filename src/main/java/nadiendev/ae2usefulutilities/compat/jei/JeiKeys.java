package nadiendev.ae2usefulutilities.compat.jei;

import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class JeiKeys {

    private JeiKeys() {
    }

    @Nullable
    public static GenericStack toStack(ITypedIngredient<?> typed) {
        Optional<ItemStack> item = typed.getIngredient(VanillaTypes.ITEM_STACK);
        if (item.isPresent()) {
            return GenericStack.fromItemStack(item.get());
        }
        Optional<FluidStack> fluid = typed.getIngredient(NeoForgeTypes.FLUID_STACK);
        if (fluid.isPresent()) {
            return GenericStack.fromFluidStack(fluid.get());
        }
        return null;
    }

    @Nullable
    public static AEKey keyOf(ITypedIngredient<?> typed) {
        GenericStack stack = toStack(typed);
        return stack == null ? null : stack.what();
    }

    @Nullable
    public static ItemStack toFilterStack(ITypedIngredient<?> typed) {
        Optional<ItemStack> item = typed.getIngredient(VanillaTypes.ITEM_STACK);
        if (item.isPresent()) {
            return item.get();
        }
        GenericStack stack = toStack(typed);
        return stack == null ? null : GenericStack.wrapInItemStack(stack);
    }

    @Nullable
    public static ITypedIngredient<?> toIngredient(IIngredientManager manager, GenericStack stack) {
        AEKey key = stack.what();
        if (key instanceof AEItemKey itemKey) {
            return manager.createTypedIngredient(VanillaTypes.ITEM_STACK, itemKey.toStack(), false).orElse(null);
        }
        if (key instanceof AEFluidKey fluidKey) {
            FluidStack fluid = fluidKey.toStack((int) Math.max(1, Math.min(stack.amount(), Integer.MAX_VALUE)));
            return manager.createTypedIngredient(NeoForgeTypes.FLUID_STACK, fluid, false).orElse(null);
        }
        return null;
    }

    public static List<List<GenericStack>> inputs(IRecipeSlotsView slots) {
        return slots.getSlotViews(RecipeIngredientRole.INPUT).stream().map(JeiKeys::ofSlot).toList();
    }

    public static List<GenericStack> outputs(IRecipeSlotsView slots) {
        return slots.getSlotViews(RecipeIngredientRole.OUTPUT).stream()
                .flatMap(slot -> ofSlot(slot).stream().limit(1))
                .toList();
    }

    public static List<GenericStack> ofSlot(IRecipeSlotView slot) {
        return slot.getAllIngredients()
                .map(JeiKeys::toStack)
                .filter(Objects::nonNull)
                .toList();
    }
}
