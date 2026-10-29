package nadiendev.ae2usefulutilities.compat.jei.category;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ItemModText;
import appeng.decorative.solid.BuddingCertusQuartzBlock;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.List;

public class CertusGrowthCategory implements IRecipeCategory<CertusGrowthCategory.Page> {

    public static final RecipeType<Page> TYPE = RecipeType.create(PatternNetwork.MODID, "certus_growth", Page.class);

    private static final int WIDTH = 150;
    private static final int HEIGHT = 60;
    private static final int CENTER_X = WIDTH / 2;
    private static final int HEADER_HEIGHT = 27;
    private static final int BODY_COLOR = 0xFF7E7E7E;

    private final List<ItemStack> buddingQuartzVariants = List.of(
            AEBlocks.DAMAGED_BUDDING_QUARTZ.stack(),
            AEBlocks.CHIPPED_BUDDING_QUARTZ.stack(),
            AEBlocks.FLAWED_BUDDING_QUARTZ.stack(),
            AEBlocks.FLAWLESS_BUDDING_QUARTZ.stack());

    private final List<ItemStack> buddingQuartzDecayOrder = List.of(
            AEBlocks.QUARTZ_BLOCK.stack(),
            AEBlocks.DAMAGED_BUDDING_QUARTZ.stack(),
            AEBlocks.CHIPPED_BUDDING_QUARTZ.stack(),
            AEBlocks.FLAWED_BUDDING_QUARTZ.stack());

    private final List<ItemStack> budGrowthStages = List.of(
            AEBlocks.SMALL_QUARTZ_BUD.stack(),
            AEBlocks.MEDIUM_QUARTZ_BUD.stack(),
            AEBlocks.LARGE_QUARTZ_BUD.stack(),
            AEBlocks.QUARTZ_CLUSTER.stack());

    private final IDrawable icon;

    public CertusGrowthCategory(IJeiHelpers helpers) {
        IGuiHelper guiHelper = helpers.getGuiHelper();
        this.icon = new CertusCyclingDrawable(Arrays.stream(new ItemLike[] {
                AEBlocks.SMALL_QUARTZ_BUD,
                AEBlocks.MEDIUM_QUARTZ_BUD,
                AEBlocks.LARGE_QUARTZ_BUD,
                AEBlocks.QUARTZ_CLUSTER
        }).map(guiHelper::createDrawableItemLike).toList());
    }

    public static void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(TYPE, List.of(Page.values()));
    }

    public static void registerCatalysts(IRecipeCatalystRegistration registration) {
    }

    @Override
    public RecipeType<Page> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return ItemModText.CERTUS_QUARTZ_GROWTH.text();
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Page page, IFocusGroup focuses) {
        switch (page) {
            case BUD_GROWTH -> {
                slot(builder, RecipeIngredientRole.CATALYST, CENTER_X - 40, 25, buddingQuartzVariants);
                slot(builder, RecipeIngredientRole.OUTPUT, CENTER_X + 40 - 18, 25, budGrowthStages);
            }
            case BUD_LOOT -> {
                slot(builder, RecipeIngredientRole.INPUT, CENTER_X - 40, 25, List.of(
                        AEBlocks.SMALL_QUARTZ_BUD.stack(),
                        AEBlocks.MEDIUM_QUARTZ_BUD.stack(),
                        AEBlocks.LARGE_QUARTZ_BUD.stack()));
                slot(builder, RecipeIngredientRole.OUTPUT, CENTER_X + 40 - 18, 25,
                        List.of(AEItems.CERTUS_QUARTZ_DUST.stack()));
            }
            case CLUSTER_LOOT -> {
                slot(builder, RecipeIngredientRole.INPUT, CENTER_X - 40, 25,
                        List.of(AEBlocks.QUARTZ_CLUSTER.stack()));
                slot(builder, RecipeIngredientRole.OUTPUT, CENTER_X + 40 - 18, 25,
                        List.of(AEItems.CERTUS_QUARTZ_CRYSTAL.stack(4)));
            }
            case BUDDING_QUARTZ_DECAY -> linkedDecaySlots(builder, 30);
            case BUDDING_QUARTZ_MOVING -> linkedDecaySlots(builder, 0);
            case GETTING_BUDDING_QUARTZ -> {
                slot(builder, RecipeIngredientRole.INPUT, 1, 1, buddingQuartzDecayOrder);
                slot(builder, RecipeIngredientRole.INPUT, 1, 22,
                        List.of(AEItems.CERTUS_QUARTZ_CRYSTAL_CHARGED.stack()));
                slot(builder, RecipeIngredientRole.CATALYST, 1, 43,
                        List.of(AEItems.METEORITE_COMPASS.stack()));
            }
            case FLAWLESS_BUDDING_QUARTZ -> {
                slot(builder, RecipeIngredientRole.CATALYST, 1, 15,
                        List.of(AEBlocks.FLAWLESS_BUDDING_QUARTZ.stack()));
                slot(builder, RecipeIngredientRole.INPUT, 1, 35,
                        List.of(AEItems.METEORITE_COMPASS.stack()));
            }
            case BUDDING_QUARTZ_ACCELERATION -> {
                slot(builder, RecipeIngredientRole.INPUT, CENTER_X - 8 - 16, 40, buddingQuartzVariants);
                slot(builder, RecipeIngredientRole.CATALYST, CENTER_X + 8, 40,
                        List.of(AEBlocks.GROWTH_ACCELERATOR.stack()));
            }
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, Page page, IFocusGroup focuses) {
        switch (page) {
            case BUD_GROWTH -> {
                header(builder, ItemModText.QUARTZ_BUDS_GROW_ON_BUDDING_QUARTZ.text());
                builder.addRecipeArrow().setPosition(CENTER_X - 12, 25);
            }
            case BUD_LOOT -> {
                header(builder, ItemModText.BUDS_DROP_DUST_WHEN_NOT_FULLY_GROWN.text());
                builder.addRecipeArrow().setPosition(CENTER_X - 12, 25);
            }
            case CLUSTER_LOOT -> {
                header(builder, ItemModText.FULLY_GROWN_BUDS_DROP_CRYSTALS.text());
                builder.addRecipeArrow().setPosition(CENTER_X - 12, 25);
                centered(builder, ItemModText.FORTUNE_APPLIES.text(), 50);
            }
            case BUDDING_QUARTZ_DECAY -> {
                header(builder, ItemModText.IMPERFECT_BUDDING_QUARTZ_DECAYS.text());
                builder.addRecipeArrow().setPosition(CENTER_X - 12, 30);
                int decayChancePct = 100 / BuddingCertusQuartzBlock.DECAY_CHANCE;
                centered(builder, ItemModText.DECAY_CHANCE.text(decayChancePct), 50);
            }
            case BUDDING_QUARTZ_MOVING -> {
                builder.addRecipeArrow().setPosition(CENTER_X - 12, 0);
                builder.addScrollBoxWidget(WIDTH, HEIGHT - 20, 0, 20)
                        .setContents(List.of(
                                ItemModText.BUDDING_QUARTZ_DECAYS_WHEN_BROKEN.text().withColor(BODY_COLOR),
                                ItemModText.SILK_TOUCH_PREVENTS_DECAY_FOR_IMPERFECT.text().withColor(BODY_COLOR),
                                ItemModText.SPATIAL_IO_NEVER_CAUSES_ANY_DECAY.text().withColor(BODY_COLOR)));
            }
            case GETTING_BUDDING_QUARTZ ->
                    sideText(builder, ItemModText.BUDDING_QUARTZ_CREATION_AND_WORLDGEN.text());
            case FLAWLESS_BUDDING_QUARTZ ->
                    sideText(builder, ItemModText.FLAWLESS_BUDDING_QUARTZ_DESCRIPTION.text());
            case BUDDING_QUARTZ_ACCELERATION -> {
                header(builder, ItemModText.CRYSTAL_GROWTH_ACCELERATORS_EFFECT.text());
                builder.addRecipePlusSign().setPosition(CENTER_X - 6, 41);
            }
        }
    }

    private void linkedDecaySlots(IRecipeLayoutBuilder builder, int y) {
        IRecipeSlotBuilder input = slot(builder, RecipeIngredientRole.INPUT, CENTER_X - 40, y,
                buddingQuartzVariants);
        IRecipeSlotBuilder output = slot(builder, RecipeIngredientRole.OUTPUT, CENTER_X + 40 - 18, y,
                buddingQuartzDecayOrder);
        builder.createFocusLink(input, output);
    }

    private static IRecipeSlotBuilder slot(IRecipeLayoutBuilder builder, RecipeIngredientRole role, int x, int y,
            List<ItemStack> stacks) {
        return builder.addSlot(role, x, y)
                .setStandardSlotBackground()
                .addItemStacks(stacks);
    }

    private static void header(IRecipeExtrasBuilder builder, Component text) {
        builder.addText(text, WIDTH, HEADER_HEIGHT)
                .setPosition(0, 0)
                .setTextAlignment(HorizontalAlignment.CENTER)
                .setColor(BODY_COLOR);
    }

    private static void centered(IRecipeExtrasBuilder builder, Component text, int y) {
        builder.addText(text, WIDTH, 10)
                .setPosition(0, y)
                .setTextAlignment(HorizontalAlignment.CENTER)
                .setColor(BODY_COLOR);
    }

    private static void sideText(IRecipeExtrasBuilder builder, Component text) {
        builder.addText(text, WIDTH - 22, HEIGHT)
                .setPosition(22, 0)
                .setTextAlignment(VerticalAlignment.CENTER)
                .setColor(BODY_COLOR);
    }

    public enum Page {
        BUD_GROWTH,
        BUD_LOOT,
        CLUSTER_LOOT,
        BUDDING_QUARTZ_DECAY,
        BUDDING_QUARTZ_MOVING,
        GETTING_BUDDING_QUARTZ,
        FLAWLESS_BUDDING_QUARTZ,
        BUDDING_QUARTZ_ACCELERATION
    }

    private static final class CertusCyclingDrawable implements IDrawable {

        private static final long FRAME_MILLIS = 2000;

        private final List<IDrawable> stages;
        private long nextFrame;
        private int currentStage;

        private CertusCyclingDrawable(List<IDrawable> stages) {
            this.stages = stages;
        }

        @Override
        public int getWidth() {
            return 16;
        }

        @Override
        public int getHeight() {
            return 16;
        }

        @Override
        public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
            long now = System.currentTimeMillis();
            if (now > nextFrame + FRAME_MILLIS) {
                currentStage++;
                nextFrame = now;
            }
            if (currentStage >= stages.size()) {
                currentStage = 0;
            }
            stages.get(currentStage).draw(guiGraphics, xOffset, yOffset);
        }
    }
}
