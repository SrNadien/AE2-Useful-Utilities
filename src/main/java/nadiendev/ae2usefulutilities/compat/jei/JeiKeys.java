package nadiendev.ae2usefulutilities.compat.jei;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

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
}
