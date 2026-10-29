package nadiendev.ae2usefulutilities.pattern.client;

import nadiendev.ae2usefulutilities.pattern.ProviderEntry;
import nadiendev.ae2usefulutilities.pattern.ProviderKey;
import nadiendev.ae2usefulutilities.pattern.network.UploadToProviderC2SPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class ProviderSelectScreen extends Screen {

    private static final int MIN_ROWS = 3;
    private static final int MAX_ROWS = 12;
    private static final int ROW_HEIGHT = 22;
    private static final int LIST_WIDTH = 260;
    private static final int SIDE_BUTTON = 20;
    private static final int GAP = 2;
    private static final int TOTAL_WIDTH = SIDE_BUTTON + GAP + LIST_WIDTH;
    private static final int RESERVED_HEIGHT = 130;
    private static final Pattern CHUNKS = Pattern.compile("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");

    private record ProviderGroup(ProviderKey key, Component name, int freeSlots, int count) {
        String plainName() {
            return name.getString();
        }
    }

    private final List<ProviderGroup> all;
    @Nullable
    private final Screen parent;
    private final String initialFilter;

    private List<ProviderGroup> filtered;
    private final List<Button> rowButtons = new ArrayList<>();
    private final List<Button> pinButtons = new ArrayList<>();
    @Nullable
    private EditBox search;
    @Nullable
    private Button clearSearch;
    @Nullable
    private Button prevPage;
    @Nullable
    private Button nextPage;
    private int rows = MIN_ROWS;
    private int scroll;
    private int pageLabelY;

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
                            existing.freeSlots() > 0 ? existing.key() : added.key(),
                            existing.name(),
                            existing.freeSlots() + added.freeSlots(),
                            existing.count() + added.count()));
        }
        return List.copyOf(byName.values());
    }

    private static int compareNatural(String a, String b) {
        String[] left = CHUNKS.split(a.toLowerCase(Locale.ROOT));
        String[] right = CHUNKS.split(b.toLowerCase(Locale.ROOT));
        int shared = Math.min(left.length, right.length);
        for (int i = 0; i < shared; i++) {
            String x = left[i];
            String y = right[i];
            int result;
            if (Character.isDigit(x.charAt(0)) && Character.isDigit(y.charAt(0))) {
                String nx = x.replaceFirst("^0+(?=.)", "");
                String ny = y.replaceFirst("^0+(?=.)", "");
                result = nx.length() != ny.length() ? Integer.compare(nx.length(), ny.length()) : nx.compareTo(ny);
            } else {
                result = x.compareTo(y);
            }
            if (result != 0) {
                return result;
            }
        }
        return Integer.compare(left.length, right.length);
    }

    @Override
    protected void init() {
        this.rows = Mth.clamp((this.height - RESERVED_HEIGHT) / ROW_HEIGHT, MIN_ROWS, MAX_ROWS);
        int left = (this.width - TOTAL_WIDTH) / 2;
        int listLeft = left + SIDE_BUTTON + GAP;
        int top = Math.max(30, (this.height - (this.rows * ROW_HEIGHT + 96)) / 2);

        this.search = new EditBox(this.font, left, top, TOTAL_WIDTH - SIDE_BUTTON - GAP, 20,
                Component.translatable("ae2usefulutilities.pattern.picker.search"));
        this.search.setHint(Component.translatable("ae2usefulutilities.pattern.picker.search")
                .withStyle(ChatFormatting.DARK_GRAY));
        addRenderableWidget(this.search);
        setInitialFocus(this.search);

        this.clearSearch = Button.builder(Component.literal("✕"), btn -> clearSearch())
                .bounds(left + TOTAL_WIDTH - SIDE_BUTTON, top, SIDE_BUTTON, 20)
                .tooltip(Tooltip.create(Component.translatable("ae2usefulutilities.pattern.picker.clear")))
                .build();
        addRenderableWidget(this.clearSearch);

        this.rowButtons.clear();
        this.pinButtons.clear();
        for (int i = 0; i < this.rows; i++) {
            int index = i;
            int y = top + 26 + i * ROW_HEIGHT;
            Button pin = Button.builder(CommonComponents.EMPTY, btn -> togglePin(index))
                    .bounds(left, y, SIDE_BUTTON, ROW_HEIGHT - 2)
                    .build();
            Button row = Button.builder(CommonComponents.EMPTY, btn -> choose(index))
                    .bounds(listLeft, y, LIST_WIDTH, ROW_HEIGHT - 2)
                    .build();
            this.pinButtons.add(pin);
            this.rowButtons.add(row);
            addRenderableWidget(pin);
            addRenderableWidget(row);
        }

        int navY = top + 28 + this.rows * ROW_HEIGHT;
        this.pageLabelY = navY + 6;
        this.prevPage = Button.builder(Component.literal("◀"), btn -> scrollBy(-this.rows))
                .bounds(left, navY, 40, 20)
                .tooltip(Tooltip.create(Component.translatable("ae2usefulutilities.pattern.picker.prev")))
                .build();
        this.nextPage = Button.builder(Component.literal("▶"), btn -> scrollBy(this.rows))
                .bounds(left + TOTAL_WIDTH - 40, navY, 40, 20)
                .tooltip(Tooltip.create(Component.translatable("ae2usefulutilities.pattern.picker.next")))
                .build();
        addRenderableWidget(this.prevPage);
        addRenderableWidget(this.nextPage);
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, btn -> onClose())
                .bounds(left + TOTAL_WIDTH / 2 - 50, navY + 24, 100, 20).build());

        this.search.setValue(this.initialFilter);
        this.search.setResponder(text -> {
            this.scroll = 0;
            applyFilter(text);
        });
        applyFilter(this.initialFilter);
    }

    private void clearSearch() {
        if (this.search != null) {
            this.search.setValue("");
            setFocused(this.search);
        }
    }

    private void applyFilter(String text) {
        String needle = text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
        List<ProviderGroup> matches = new ArrayList<>();
        for (ProviderGroup entry : this.all) {
            if (needle.isEmpty() || entry.plainName().toLowerCase(Locale.ROOT).contains(needle)) {
                matches.add(entry);
            }
        }
        matches.sort(Comparator
                .comparing((ProviderGroup entry) -> !PinnedProviders.isPinned(entry.plainName()))
                .thenComparing((a, b) -> compareNatural(a.plainName(), b.plainName())));
        this.filtered = matches;
        clampScroll();
        refreshRows();
    }

    private void clampScroll() {
        this.scroll = Math.max(0, Math.min(this.scroll, Math.max(0, this.filtered.size() - this.rows)));
    }

    private void scrollBy(int delta) {
        this.scroll += delta;
        clampScroll();
        refreshRows();
    }

    private void refreshRows() {
        if (this.rowButtons.size() < this.rows || this.prevPage == null || this.nextPage == null
                || this.search == null || this.clearSearch == null) {
            return;
        }
        for (int i = 0; i < this.rows; i++) {
            Button row = this.rowButtons.get(i);
            Button pin = this.pinButtons.get(i);
            ProviderGroup entry = entryAt(i);
            if (entry == null) {
                row.visible = false;
                pin.visible = false;
                continue;
            }
            boolean pinned = PinnedProviders.isPinned(entry.plainName());
            row.visible = true;
            row.active = entry.freeSlots() > 0;
            row.setMessage(Component.empty()
                    .append(entry.name())
                    .append(Component.literal("  ")
                            .append(Component.translatable("ae2usefulutilities.pattern.picker.slots",
                                    entry.freeSlots(), entry.count()))
                            .withStyle(entry.freeSlots() > 0 ? ChatFormatting.GRAY : ChatFormatting.DARK_RED)));
            pin.visible = true;
            pin.setMessage(Component.literal(pinned ? "★" : "☆")
                    .withStyle(pinned ? ChatFormatting.GOLD : ChatFormatting.GRAY));
            pin.setTooltip(Tooltip.create(Component.translatable(pinned
                    ? "ae2usefulutilities.pattern.picker.unpin"
                    : "ae2usefulutilities.pattern.picker.pin")));
        }
        this.prevPage.active = this.scroll > 0;
        this.nextPage.active = this.scroll + this.rows < this.filtered.size();
        this.clearSearch.active = !this.search.getValue().isEmpty();
    }

    @Nullable
    private ProviderGroup entryAt(int row) {
        int index = this.scroll + row;
        return index >= 0 && index < this.filtered.size() ? this.filtered.get(index) : null;
    }

    private void choose(int row) {
        upload(entryAt(row));
    }

    private void togglePin(int row) {
        ProviderGroup entry = entryAt(row);
        if (entry == null || this.search == null) {
            return;
        }
        PinnedProviders.toggle(entry.plainName());
        applyFilter(this.search.getValue());
    }

    private void upload(@Nullable ProviderGroup entry) {
        if (entry == null || entry.freeSlots() <= 0) {
            return;
        }
        PacketDistributor.sendToServer(new UploadToProviderC2SPacket(entry.key()));
        onClose();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 1) {
            if (this.search != null && this.search.isMouseOver(mouseX, mouseY)) {
                clearSearch();
                return true;
            }
            for (int i = 0; i < this.rows; i++) {
                Button row = this.rowButtons.get(i);
                if (row.visible && row.isMouseOver(mouseX, mouseY)) {
                    togglePin(i);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY != 0 && this.filtered.size() > this.rows) {
            scrollBy(scrollY > 0 ? -1 : 1);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if ((keyCode == 257 || keyCode == 335) && this.filtered.size() == 1) {
            upload(this.filtered.getFirst());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);
        if (this.filtered.isEmpty()) {
            guiGraphics.drawCenteredString(this.font,
                    Component.translatable("ae2usefulutilities.pattern.picker.empty"),
                    this.width / 2, this.height / 2, 0xFF5555);
        } else {
            int pages = Math.max(1, (this.filtered.size() + this.rows - 1) / this.rows);
            int page = Math.min(pages, this.scroll / this.rows + 1);
            guiGraphics.drawCenteredString(this.font,
                    Component.translatable("ae2usefulutilities.pattern.picker.page", page, pages,
                            this.filtered.size()),
                    this.width / 2, this.pageLabelY, 0xA0A0A0);
        }
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
