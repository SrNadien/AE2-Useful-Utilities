package nadiendev.ae2usefulutilities.compat.jei.category;

import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ItemModText;
import appeng.items.tools.powered.EntropyManipulatorItem;
import appeng.recipes.AERecipeTypes;
import appeng.recipes.entropy.EntropyRecipe;
import com.mojang.datafixers.util.Either;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class EntropyManipulatorCategory extends AbstractRecipeCategory<RecipeHolder<EntropyRecipe>> {

    public static final RecipeType<RecipeHolder<EntropyRecipe>> TYPE = RecipeType
            .createRecipeHolderType(ResourceLocation.fromNamespaceAndPath(PatternNetwork.MODID, "entropy"));

    private static final ResourceLocation TEXTURE = AppEng.makeId("textures/guis/jei.png");
    private static final int WIDTH = 130;
    private static final int CENTER_X = WIDTH / 2;
    private static final int TEXT_COLOR = 0xFF404040;

    private final IDrawable blockDestroyOverlay;
    private final IDrawable iconHeat;
    private final IDrawable iconCool;

    public EntropyManipulatorCategory(IJeiHelpers helpers) {
        super(TYPE, AEItems.ENTROPY_MANIPULATOR.asItem().getDescription(),
                helpers.getGuiHelper()
                        .drawableBuilder(AppEng.makeId("textures/item/entropy_manipulator.png"), 0, 0, 16, 16)
                        .setTextureSize(16, 16)
                        .build(),
                WIDTH, 50);
        IGuiHelper guiHelper = helpers.getGuiHelper();
        this.blockDestroyOverlay = guiHelper.createDrawable(TEXTURE, 0, 52, 16, 16);
        this.iconHeat = guiHelper.createDrawable(TEXTURE, 0, 68, 6, 6);
        this.iconCool = guiHelper.createDrawable(TEXTURE, 6, 68, 6, 6);
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        registration.addRecipes(TYPE, level.getRecipeManager().getAllRecipesFor(AERecipeTypes.ENTROPY));
    }

    public static void registerCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(AEItems.ENTROPY_MANIPULATOR.stack(), TYPE);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<EntropyRecipe> holder, IFocusGroup focuses) {
        var recipe = holder.value();

        var input = builder.addInputSlot(CENTER_X - 36, 15)
                .setStandardSlotBackground();
        var inputFluid = recipe.getInput().fluid().map(EntropyRecipe.FluidInput::fluid).orElse(null);
        var inputBlock = recipe.getInput().block().map(EntropyRecipe.BlockInput::block).orElse(null);
        setFluidOrBlockSlot(input, inputBlock, inputFluid);

        int x = CENTER_X + 20;

        var outputBlock = recipe.getOutput().block().map(EntropyRecipe.BlockOutput::block).orElse(null);
        var outputFluid = recipe.getOutput().fluid().map(EntropyRecipe.FluidOutput::fluid).orElse(null);
        if (outputFluid == Fluids.EMPTY) {
            outputFluid = null;
        }

        if (outputBlock == Blocks.AIR && outputFluid == null) {
            var destroyed = builder.addSlot(RecipeIngredientRole.RENDER_ONLY, x, 15)
                    .setStandardSlotBackground();
            setFluidOrBlockSlot(destroyed, inputBlock, inputFluid);
            destroyed.setOverlay(blockDestroyOverlay, 0, 0);
            destroyed.addRichTooltipCallback((slotView, tooltip) -> tooltip.add(
                    ItemModText.CONSUMED.text().withStyle(ChatFormatting.RED, ChatFormatting.BOLD)));
            x += 18;
        } else if (outputBlock != null || outputFluid != null) {
            var output = builder.addOutputSlot(x, 15)
                    .setStandardSlotBackground();
            setFluidOrBlockSlot(output, outputBlock, outputFluid);
            x += 18;
        }

        for (var drop : recipe.getDrops()) {
            builder.addOutputSlot(x, 15)
                    .setStandardSlotBackground()
                    .addItemStack(drop.copy());
            x += 18;
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<EntropyRecipe> holder,
            IFocusGroup focuses) {
        var mode = holder.value().getMode();
        IDrawable modeIcon = switch (mode) {
            case HEAT -> iconHeat;
            case COOL -> iconCool;
        };
        Component modeText = switch (mode) {
            case HEAT -> ItemModText.ENTROPY_MANIPULATOR_HEAT.text(EntropyManipulatorItem.ENERGY_PER_USE);
            case COOL -> ItemModText.ENTROPY_MANIPULATOR_COOL.text(EntropyManipulatorItem.ENERGY_PER_USE);
        };
        Component interaction = switch (mode) {
            case HEAT -> ItemModText.RIGHT_CLICK.text();
            case COOL -> ItemModText.SHIFT_RIGHT_CLICK.text();
        };

        builder.addRecipeArrow(CENTER_X - 12, 14);

        int modeLabelX = CENTER_X - 36;
        builder.addText(modeText, WIDTH - modeLabelX, 10)
                .setPosition(modeLabelX, 0)
                .setColor(TEXT_COLOR);
        builder.addDrawable(modeIcon, modeLabelX - 9, 1);

        builder.addText(interaction, WIDTH, 10)
                .setPosition(0, 38)
                .setTextAlignment(HorizontalAlignment.CENTER)
                .setColor(TEXT_COLOR);
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<EntropyRecipe> holder) {
        return holder.id();
    }

    private void setFluidOrBlockSlot(IRecipeSlotBuilder slot, @Nullable Block block, @Nullable Fluid fluid) {
        if (fluid != null && fluid != Fluids.EMPTY) {
            if (!fluid.isSource(fluid.defaultFluidState())) {
                if (fluid instanceof FlowingFluid flowingFluid) {
                    slot.addFluidStack(flowingFluid.getSource());
                } else {
                    slot.addFluidStack(fluid);
                }
                slot.addRichTooltipCallback((slotView, tooltip) -> {
                    var lines = tooltip.getLines();
                    if (!lines.isEmpty()) {
                        Component name = ItemModText.FLOWING_FLUID_NAME.text(fluid.getFluidType().getDescription());
                        lines.set(0, Either.<FormattedText, TooltipComponent>left(name));
                    }
                });
            } else {
                slot.addFluidStack(fluid);
            }
        } else if (block != null && !block.asItem().getDefaultInstance().isEmpty()) {
            slot.addItemLike(block);
        }
    }
}
