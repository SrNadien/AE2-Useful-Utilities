package nadiendev.ae2usefulutilities.compat.jei.category;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ItemModText;
import appeng.integration.modules.itemlists.FluidBlockRendering;
import appeng.recipes.AERecipeTypes;
import appeng.recipes.transform.TransformRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.AbstractRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class TransformCategory extends AbstractRecipeCategory<RecipeHolder<TransformRecipe>> {

    public static final RecipeType<RecipeHolder<TransformRecipe>> TYPE = RecipeType
            .createRecipeHolderType(ResourceLocation.fromNamespaceAndPath(PatternNetwork.MODID, "transform"));

    private static final int ROW_Y = 23;
    private static final int TEXT_COLOR = 0xFF404040;

    private final IIngredientRenderer<FluidStack> fluidRenderer = new FluidBlockRenderer();

    public TransformCategory(IJeiHelpers helpers) {
        super(TYPE, ItemModText.TRANSFORM_CATEGORY.text(),
                helpers.getGuiHelper().createDrawableItemLike(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED), 130, 62);
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        var level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        registration.addRecipes(TYPE, level.getRecipeManager().getAllRecipesFor(AERecipeTypes.TRANSFORM));
    }

    public static void registerCatalysts(IRecipeCatalystRegistration registration) {
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<TransformRecipe> holder, IFocusGroup focuses) {
        var recipe = holder.value();
        var x = 5;
        var y = 5;
        if (recipe.ingredients.size() < 3) {
            y += (3 - recipe.ingredients.size()) * 18 / 2;
        }
        for (var input : recipe.ingredients) {
            builder.addInputSlot(x + 1, y + 1)
                    .setStandardSlotBackground()
                    .addIngredients(input);
            y += 18;
            if (y >= 54) {
                y -= 54;
                x += 18;
            }
        }

        if (recipe.circumstance.isFluid()) {
            var slot = builder.addSlot(RecipeIngredientRole.CATALYST, 56, ROW_Y + 1);
            for (var fluid : recipe.circumstance.getFluidsForRendering()) {
                if (fluid.isSource(fluid.defaultFluidState())) {
                    slot.addFluidStack(fluid);
                }
            }
            slot.setCustomRenderer(NeoForgeTypes.FLUID_STACK, fluidRenderer);
        } else if (recipe.circumstance.isExplosion()) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 56, ROW_Y + 1)
                    .addItemLike(Blocks.TNT)
                    .addItemLike(AEBlocks.TINY_TNT);
        }

        builder.addOutputSlot(106, ROW_Y + 1)
                .setStandardSlotBackground()
                .addItemStack(recipe.output.copy());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<TransformRecipe> holder,
            IFocusGroup focuses) {
        var recipe = holder.value();

        builder.addRecipeArrow(27, ROW_Y);
        builder.addRecipeArrow(80, ROW_Y);

        Component circumstance = recipe.circumstance.isExplosion()
                ? ItemModText.EXPLOSION.text()
                : ItemModText.SUBMERGE_IN.text();
        builder.addText(circumstance, getWidth() - 48, 20)
                .setPosition(24, 0)
                .setTextAlignment(HorizontalAlignment.CENTER)
                .setTextAlignment(VerticalAlignment.CENTER)
                .setColor(TEXT_COLOR);
    }

    @Override
    public ResourceLocation getRegistryName(RecipeHolder<TransformRecipe> holder) {
        return holder.id();
    }

    private static final class FluidBlockRenderer implements IIngredientRenderer<FluidStack> {
        @Override
        public void render(GuiGraphics graphics, FluidStack ingredient) {
            FluidBlockRendering.render(graphics, ingredient.getFluid(), 0, 0, 16, 16);
        }

        @Override
        public void getTooltip(ITooltipBuilder tooltip, FluidStack ingredient, TooltipFlag tooltipFlag) {
            tooltip.add(ingredient.getHoverName());
        }

        @Override
        @SuppressWarnings("removal")
        public List<Component> getTooltip(FluidStack ingredient, TooltipFlag tooltipFlag) {
            return List.of(ingredient.getHoverName());
        }
    }
}
