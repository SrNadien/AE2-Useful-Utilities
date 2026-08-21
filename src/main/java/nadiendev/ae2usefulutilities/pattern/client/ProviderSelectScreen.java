package nadiendev.ae2usefulutilities.pattern.client;

import nadiendev.ae2usefulutilities.pattern.ProviderEntry;
import nadiendev.ae2usefulutilities.pattern.ProviderKey;
import nadiendev.ae2usefulutilities.pattern.network.UploadToProviderC2SPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

public class ProviderSelectScreen extends Screen {

    private static final int ROWS = 9;
    private static final int ROW_HEIGHT = 20;
    private static final int LIST_WIDTH = 260;

    /**
     * Identical providers collapse into one row: picking it fills the whole bank, because the server spills
     * overflow into the other providers sharing the name.
     */
    private record ProviderGroup(ProviderKey key, Component name, int freeSlots, int count) {
    }

    private final List<ProviderGroup> all;
    @Nullable
    private final Screen parent;
    private final String initialFilter;

    private List<ProviderGroup> filtered;
    private final List<Button> rowButtons = new ArrayList<>();
    @Nullable
    private EditBox search;
    @Nullable
    private Button scrollUp;
    @Nullable
    private Button scrollDown;
    private int scroll;

    public ProviderSelectScreen(List<ProviderEntry> entries, @Nullable Screen parent, String initialFilter) {
        super(Component.translatable("ae2usefulutilities.pattern.picker.title"));
        this.all = group(entries);
        this.parent = parent;
        this.initialFilter = initialFilter == null ? "" : initialFilter;
        this.filtered = this.all;
    }

    private static List<ProviderGroup> group(List<ProviderEntry> entries) {
        Map<String, ProviderGroup> byName = new LinkedHashMap<>();
        for (ProviderEntry entry : entries) {
            byName.merge(entry.name().getString(),
                    new ProviderGroup(entry.key(), entry.name(), entry.freeSlots(), 1),
                    (existing, added) -> new ProviderGroup(
                            // Point the row at a provider that still has room, so uploads start where they fit.
                            existing.freeSlots() > 0 ? existing.key() : added.key(),
                            existing.name(),
                            existing.freeSlots() + added.freeSlots(),
                            existing.count() + added.count()));
        }
        return List.copyOf(byName.values());
    }

    @Override
    protected void init() {
        int left = (this.width - LIST_WIDTH) / 2;
        int top = Math.max(40, (this.height - (ROWS * ROW_HEIGHT + 70)) / 2);

        this.search = new EditBox(this.font, left, top, LIST_WIDTH, 18,
                Component.translatable("ae2usefulutilities.pattern.picker.search"));
        this.search.setHint(Component.translatable("ae2usefulutilities.pattern.picker.search")
                .withStyle(ChatFormatting.DARK_GRAY));
        addRenderableWidget(this.search);
        setInitialFocus(this.search);

        this.rowButtons.clear();
        for (int i = 0; i < ROWS; i++) {
            int index = i;
            Button row = Button.builder(CommonComponents.EMPTY, btn -> choose(index))
                    .bounds(left, top + 24 + i * ROW_HEIGHT, LIST_WIDTH, ROW_HEIGHT - 2)
                    .build();
            this.rowButtons.add(row);
            addRenderableWidget(row);
        }

        int navY = top + 26 + ROWS * ROW_HEIGHT;
        this.scrollUp = Button.builder(Component.literal("▲"), btn -> scrollBy(-ROWS))
                .bounds(left, navY, 40, 20).build();
        this.scrollDown = Button.builder(Component.literal("▼"), btn -> scrollBy(ROWS))
                .bounds(left + LIST_WIDTH - 40, navY, 40, 20).build();
        addRenderableWidget(this.scrollUp);
        addRenderableWidget(this.scrollDown);
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, btn -> onClose())
                .bounds(left + LIST_WIDTH / 2 - 50, navY, 100, 20).build());

        // Every widget exists now, so the responder is safe to attach.
        this.search.setValue(this.initialFilter);
        this.search.setResponder(text -> {
            this.scroll = 0;
            applyFilter(text);
        });
        applyFilter(this.initialFilter);
    }

    private void applyFilter(String text) {
        String needle = text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
        if (needle.isEmpty()) {
            this.filtered = this.all;
        } else {
            List<ProviderGroup> matches = new ArrayList<>();
            for (ProviderGroup entry : this.all) {
                if (entry.name().getString().toLowerCase(Locale.ROOT).contains(needle)) {
                    matches.add(entry);
                }
            }
            this.filtered = matches;
        }
        clampScroll();
        refreshRows();
    }

    private void clampScroll() {
        this.scroll = Math.max(0, Math.min(this.scroll, Math.max(0, this.filtered.size() - ROWS)));
    }

    private void scrollBy(int delta) {
        this.scroll += delta;
        clampScroll();
        refreshRows();
    }

    private void refreshRows() {
        if (this.rowButtons.size() < ROWS || this.scrollUp == null || this.scrollDown == null) {
            return;
        }
        for (int i = 0; i < ROWS; i++) {
            Button row = this.rowButtons.get(i);
            ProviderGroup entry = entryAt(i);
            if (entry == null) {
                row.visible = false;
                continue;
            }
            row.visible = true;
            row.active = entry.freeSlots() > 0;
            row.setMessage(Component.empty()
                    .append(entry.name())
                    .append(Component.literal("  ")
                            .append(Component.translatable("ae2usefulutilities.pattern.picker.slots",
                                    entry.freeSlots(), entry.count()))
                            .withStyle(entry.freeSlots() > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_RED)));
        }
        this.scrollUp.active = this.scroll > 0;
        this.scrollDown.active = this.scroll < this.filtered.size() - ROWS;
    }

    @Nullable
    private ProviderGroup entryAt(int row) {
        int index = this.scroll + row;
        return index >= 0 && index < this.filtered.size() ? this.filtered.get(index) : null;
    }

    private void choose(int row) {
        upload(entryAt(row));
    }

    private void upload(@Nullable ProviderGroup entry) {
        if (entry == null || entry.freeSlots() <= 0) {
            return;
        }
        ClientPacketDistributor.sendToServer(new UploadToProviderC2SPacket(entry.key()));
        onClose();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY != 0 && this.filtered.size() > ROWS) {
            scrollBy(scrollY > 0 ? -1 : 1);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        // Enter with the search narrowed down to a single provider uploads straight away.
        if ((event.key() == GLFW.GLFW_KEY_ENTER || event.key() == GLFW.GLFW_KEY_KP_ENTER)
                && this.filtered.size() == 1) {
            upload(this.filtered.getFirst());
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        centered(guiGraphics, this.title, 20, 0xFFFFFFFF);
        if (this.filtered.isEmpty()) {
            centered(guiGraphics, Component.translatable("ae2usefulutilities.pattern.picker.empty"),
                    this.height / 2, 0xFFFF5555);
        } else if (this.filtered.size() > ROWS) {
            centered(guiGraphics, Component.literal((this.scroll + 1) + "-"
                    + Math.min(this.scroll + ROWS, this.filtered.size()) + " / " + this.filtered.size()),
                    32, 0xFFA0A0A0);
        }
    }

    private void centered(GuiGraphicsExtractor guiGraphics, Component text, int y, int argb) {
        int x = this.width / 2 - this.font.width(text) / 2;
        guiGraphics.text(this.font, text, x, y, argb, true);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
