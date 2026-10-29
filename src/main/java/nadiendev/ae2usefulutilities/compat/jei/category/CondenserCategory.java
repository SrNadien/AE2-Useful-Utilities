package nadiendev.ae2usefulutilities.compat.jei.category;

import appeng.api.config.CondenserOutput;
import appeng.api.implementations.items.IStorageComponent;
import appeng.blockentity.misc.CondenserBlockEntity;
import appeng.client.gui.Icon;
import appeng.core.AppEng;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ButtonToolTips;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CondenserCategory implements IRecipeCategory<CondenserOutput> {

    public static final RecipeType<CondenserOutput> TYPE = RecipeType.create(PatternNetwork.MODID, "condenser",
            CondenserOutput.class);

    private static final int BUTTON_X = 80;
    private static final int BUTTON_Y = 26;
    private static final int BUTTON_SIZE = 16;

    private final IDrawable icon;
    private final IDrawable background;
    private final IDrawableAnimated progress;
    private final IDrawable trash;
    private final IDrawable button;
    private final Map<CondenserOutput, IDrawable> buttonIcons = new EnumMap<>(CondenserOutput.class);
    private final Map<CondenserOutput, ResourceLocation> ids = new EnumMap<>(CondenserOutput.class);

    public CondenserCategory(IJeiHelpers helpers) {
        IGuiHelper guiHelper = helpers.getGuiHelper();
        this.icon = guiHelper.createDrawableItemStack(AEBlocks.CONDENSER.stack());

        ResourceLocation texture = AppEng.makeId("textures/guis/condenser.png");
        this.background = guiHelper.createDrawable(texture, 48, 25, 96, 48);
        this.progress = guiHelper.drawableBuilder(texture, 176, 0, 6, 18)
                .addPadding(0, 0, 72, 0)
                .buildAnimated(40, IDrawableAnimated.StartDirection.BOTTOM, false);

        this.trash = new IconDrawable(Icon.BACKGROUND_TRASH);
        this.button = new IconDrawable(Icon.TOOLBAR_BUTTON_BACKGROUND);
        this.buttonIcons.put(CondenserOutput.MATTER_BALLS, new IconDrawable(Icon.CONDENSER_OUTPUT_MATTER_BALL));
        this.buttonIcons.put(CondenserOutput.SINGULARITY, new IconDrawable(Icon.CONDENSER_OUTPUT_SINGULARITY));
        for (CondenserOutput output : CondenserOutput.values()) {
            this.ids.put(output, ResourceLocation.fromNamespaceAndPath(PatternNetwork.MODID,
                    "condenser/" + output.name().toLowerCase(Locale.ROOT)));
        }
    }

    @Override
    public RecipeType<CondenserOutput> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.ae2.condenser");
    }

    @Override
    public int getWidth() {
        return 96;
    }

    @Override
    public int getHeight() {
        return 48;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public ResourceLocation getRegistryName(CondenserOutput recipe) {
        return ids.get(recipe);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CondenserOutput recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.OUTPUT, 57, 27)
                .setSlotName("output")
                .addItemStack(getOutput(recipe));
        builder.addSlot(RecipeIngredientRole.CATALYST, 53, 1)
                .setSlotName("storage_cell")
                .addItemStacks(getViableStorageComponents(recipe));
    }

    @Override
    public void draw(CondenserOutput recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
            double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
        progress.draw(guiGraphics, 0, 0);
        trash.draw(guiGraphics, 3, 27);
        button.draw(guiGraphics, BUTTON_X, BUTTON_Y);
        IDrawable buttonIcon = buttonIcons.get(recipe);
        if (buttonIcon != null) {
            buttonIcon.draw(guiGraphics, BUTTON_X + 1, BUTTON_Y + 1);
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, CondenserOutput recipe, IRecipeSlotsView recipeSlotsView,
            double mouseX, double mouseY) {
        if (mouseX < BUTTON_X || mouseX >= BUTTON_X + BUTTON_SIZE || mouseY < BUTTON_Y
                || mouseY >= BUTTON_Y + BUTTON_SIZE) {
            return;
        }
        Component text = switch (recipe) {
            case MATTER_BALLS -> ButtonToolTips.MatterBalls.text(recipe.requiredPower);
            case SINGULARITY -> ButtonToolTips.Singularity.text(recipe.requiredPower);
            default -> null;
        };
        if (text == null) {
            return;
        }
        for (String line : text.getString().split("\n")) {
            tooltip.add(Component.literal(line));
        }
    }

    private static ItemStack getOutput(CondenserOutput recipe) {
        return switch (recipe) {
            case MATTER_BALLS -> AEItems.MATTER_BALL.stack();
            case SINGULARITY -> AEItems.SINGULARITY.stack();
            default -> ItemStack.EMPTY;
        };
    }

    private static List<ItemStack> getViableStorageComponents(CondenserOutput output) {
        List<ItemStack> components = new ArrayList<>();
        addViable(output, components, AEItems.CELL_COMPONENT_1K.stack());
        addViable(output, components, AEItems.CELL_COMPONENT_4K.stack());
        addViable(output, components, AEItems.CELL_COMPONENT_16K.stack());
        addViable(output, components, AEItems.CELL_COMPONENT_64K.stack());
        addViable(output, components, AEItems.CELL_COMPONENT_256K.stack());
        return components;
    }

    private static void addViable(CondenserOutput output, List<ItemStack> components, ItemStack stack) {
        IStorageComponent component = (IStorageComponent) stack.getItem();
        int storage = component.getBytes(stack) * CondenserBlockEntity.BYTE_MULTIPLIER;
        if (storage >= output.requiredPower) {
            components.add(stack);
        }
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(TYPE, List.of(CondenserOutput.MATTER_BALLS, CondenserOutput.SINGULARITY));
    }

    public static void registerCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(AEBlocks.CONDENSER.stack(), TYPE);
    }

    private static final class IconDrawable implements IDrawable {

        private final Icon iconSource;

        private IconDrawable(Icon iconSource) {
            this.iconSource = iconSource;
        }

        @Override
        public int getWidth() {
            return iconSource.width;
        }

        @Override
        public int getHeight() {
            return iconSource.height;
        }

        @Override
        public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
            iconSource.getBlitter().dest(xOffset, yOffset).blit(guiGraphics);
        }
    }
}
