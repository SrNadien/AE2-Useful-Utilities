package nadiendev.ae2usefulutilities.compat.jei;

import appeng.api.ids.AEComponents;
import appeng.core.AEConfig;
import appeng.core.FacadeCreativeTab;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.core.definitions.ItemDefinition;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class PluginExtras {

    private PluginExtras() {
    }

    private record ComponentSubtype(List<DataComponentType<?>> types) implements ISubtypeInterpreter<ItemStack> {

        @Nullable
        @Override
        public Object getSubtypeData(ItemStack stack, UidContext context) {
            if (context == UidContext.Recipe) {
                return null;
            }
            List<Object> values = new ArrayList<>(types.size());
            for (DataComponentType<?> type : types) {
                values.add(stack.get(type));
            }
            return values.stream().allMatch(Objects::isNull) ? null : values;
        }

        @Override
        public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
            Object data = getSubtypeData(stack, context);
            return data == null ? "" : data.toString();
        }
    }

    public static void registerSubtypes(ISubtypeRegistration registration) {
        ISubtypeInterpreter<ItemStack> energy = new ComponentSubtype(List.of(AEComponents.STORED_ENERGY));
        List<Item> withEnergy = List.of(
                AEItems.COLOR_APPLICATOR.asItem(),
                AEItems.CHARGED_STAFF.asItem(),
                AEItems.MATTER_CANNON.asItem(),
                AEItems.ENTROPY_MANIPULATOR.asItem(),
                AEItems.WIRELESS_TERMINAL.asItem(),
                AEItems.WIRELESS_CRAFTING_TERMINAL.asItem(),
                AEBlocks.ENERGY_CELL.asItem(),
                AEBlocks.DENSE_ENERGY_CELL.asItem(),
                AEItems.PORTABLE_ITEM_CELL1K.asItem(),
                AEItems.PORTABLE_ITEM_CELL4K.asItem(),
                AEItems.PORTABLE_ITEM_CELL16K.asItem(),
                AEItems.PORTABLE_ITEM_CELL64K.asItem(),
                AEItems.PORTABLE_ITEM_CELL256K.asItem(),
                AEItems.PORTABLE_FLUID_CELL1K.asItem(),
                AEItems.PORTABLE_FLUID_CELL4K.asItem(),
                AEItems.PORTABLE_FLUID_CELL16K.asItem(),
                AEItems.PORTABLE_FLUID_CELL64K.asItem(),
                AEItems.PORTABLE_FLUID_CELL256K.asItem());
        for (Item item : withEnergy) {
            registration.registerSubtypeInterpreter(item, energy);
        }
        registration.registerSubtypeInterpreter(AEItems.FACADE.asItem(),
                new ComponentSubtype(List.of(AEComponents.FACADE_ITEM)));
        registration.registerSubtypeInterpreter(AEParts.ANNIHILATION_PLANE.asItem(),
                new ComponentSubtype(List.of(DataComponents.ENCHANTMENTS)));
    }

    public static void registerDescriptions(IRecipeRegistration registration) {
        Component presses = Component.translatable("gui.ae2.inWorldCraftingPresses");
        for (ItemDefinition<?> press : List.of(AEItems.LOGIC_PROCESSOR_PRESS, AEItems.CALCULATION_PROCESSOR_PRESS,
                AEItems.ENGINEERING_PROCESSOR_PRESS, AEItems.SILICON_PRESS)) {
            registration.addItemStackInfo(press.stack(), presses);
        }
        registration.addItemStackInfo(AEBlocks.CRANK.stack(),
                Component.translatable("ae2.rei_jei_integration.crank_description"));
    }

    public static void hideUnwanted(IJeiRuntime runtime) {
        if (!AEConfig.instance().isDebugToolsEnabled()) {
            Collection<ItemStack> debug = List.of(
                    AEBlocks.DEBUG_CUBE_GEN.stack(),
                    AEBlocks.DEBUG_ENERGY_GEN.stack(),
                    AEBlocks.DEBUG_ITEM_GEN.stack(),
                    AEBlocks.DEBUG_PHANTOM_NODE.stack(),
                    AEItems.DEBUG_CARD.stack(),
                    AEItems.DEBUG_ERASER.stack(),
                    AEItems.DEBUG_METEORITE_PLACER.stack(),
                    AEItems.DEBUG_REPLICATOR_CARD.stack());
            runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, debug);
        }
        if (!AEConfig.instance().isEnableFacadesInRecipeViewer()) {
            runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK,
                    FacadeCreativeTab.getDisplayItems());
        }
    }
}
