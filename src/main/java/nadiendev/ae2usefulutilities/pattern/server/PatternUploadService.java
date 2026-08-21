package nadiendev.ae2usefulutilities.pattern.server;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import appeng.menu.SlotSemantics;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.parts.AEBasePart;
import nadiendev.ae2usefulutilities.config.AE2UtilitiesConfig;
import nadiendev.ae2usefulutilities.pattern.ProviderEntry;
import nadiendev.ae2usefulutilities.pattern.ProviderKey;
import nadiendev.ae2usefulutilities.pattern.network.ProviderListS2CPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PatternUploadService {

    private PatternUploadService() {
    }

    public static void sendProviderList(ServerPlayer player) {
        if (!AE2UtilitiesConfig.PATTERN_UPLOADER_ENABLED.get()) {
            return;
        }
        IGrid grid = gridOf(player);
        if (grid == null) {
            say(player, ChatFormatting.RED, "ae2usefulutilities.pattern.upload.nogrid");
            return;
        }

        List<ProviderEntry> entries = new ArrayList<>();
        for (PatternProviderLogicHost host : providers(grid)) {
            // A misbehaving modded provider must not take the whole list down with it.
            try {
                ProviderKey key = keyOf(host);
                if (key == null) {
                    continue;
                }
                InternalInventory inv = host.getLogic().getPatternInv();
                int free = 0;
                for (int i = 0; i < inv.size(); i++) {
                    if (inv.getStackInSlot(i).isEmpty()) {
                        free++;
                    }
                }
                entries.add(new ProviderEntry(key, nameOf(host), free, inv.size()));
            } catch (RuntimeException ignored) {
                // skip this provider
            }
        }

        entries.sort(Comparator.comparing((ProviderEntry e) -> e.name().getString())
                .thenComparingInt(e -> e.key().pos().hashCode()));

        PacketDistributor.sendToPlayer(player, new ProviderListS2CPacket(List.copyOf(entries)));
    }

    public static void uploadTo(ServerPlayer player, ProviderKey key) {
        if (!AE2UtilitiesConfig.PATTERN_UPLOADER_ENABLED.get()) {
            return;
        }
        if (!(player.containerMenu instanceof PatternEncodingTermMenu menu)) {
            return;
        }
        IGrid grid = gridOf(player);
        if (grid == null) {
            say(player, ChatFormatting.RED, "ae2usefulutilities.pattern.upload.nogrid");
            return;
        }

        Set<PatternProviderLogicHost> all = providers(grid);
        PatternProviderLogicHost target = null;
        for (PatternProviderLogicHost host : all) {
            if (key.equals(keyOf(host))) {
                target = host;
                break;
            }
        }
        if (target == null) {
            say(player, ChatFormatting.RED, "ae2usefulutilities.pattern.upload.gone");
            return;
        }

        // Overflow spills into other providers sharing the same name, so picking one of a bank of identical
        // machines fills the whole bank instead of stopping at the first full one.
        Component targetName = nameOf(target);
        List<PatternProviderLogicHost> candidates = new ArrayList<>();
        candidates.add(target);
        for (PatternProviderLogicHost host : all) {
            if (host != target && nameOf(host).getString().equals(targetName.getString())) {
                candidates.add(host);
            }
        }

        Inventory inventory = player.getInventory();
        int moved = 0;
        int found = 0;

        // The pattern sitting in the terminal's output slot counts too - that is usually the one just encoded.
        for (Slot slot : menu.getSlots(SlotSemantics.ENCODED_PATTERN)) {
            ItemStack stack = slot.getItem();
            if (stack.isEmpty() || !PatternDetailsHelper.isEncodedPattern(stack)) {
                continue;
            }
            found += stack.getCount();
            ItemStack remainder = insertInto(candidates, stack);
            moved += stack.getCount() - remainder.getCount();
            slot.set(remainder);
        }

        for (int slot = 0; slot < inventory.items.size(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty() || !PatternDetailsHelper.isEncodedPattern(stack)) {
                continue;
            }
            found += stack.getCount();
            ItemStack remainder = insertInto(candidates, stack);
            moved += stack.getCount() - remainder.getCount();
            inventory.setItem(slot, remainder);
        }

        player.sendSystemMessage(Component
                .translatable("ae2usefulutilities.pattern.upload.done", moved, found,
                        nameOf(target))
                .withStyle(ChatFormatting.GRAY));
    }

    private static Set<PatternProviderLogicHost> providers(IGrid grid) {
        Set<PatternProviderLogicHost> hosts = new HashSet<>();
        for (Class<?> clazz : grid.getMachineClasses()) {
            if (PatternProviderLogicHost.class.isAssignableFrom(clazz)) {
                @SuppressWarnings("unchecked")
                Class<PatternProviderLogicHost> hostClass = (Class<PatternProviderLogicHost>) clazz;
                hosts.addAll(grid.getMachines(hostClass));
            }
        }
        return hosts;
    }

    private static ItemStack insertInto(List<PatternProviderLogicHost> candidates, ItemStack stack) {
        ItemStack remaining = stack;
        for (PatternProviderLogicHost host : candidates) {
            if (remaining.isEmpty()) {
                break;
            }
            try {
                remaining = host.getLogic().getPatternInv().addItems(remaining);
            } catch (RuntimeException ignored) {
                // try the next provider
            }
        }
        return remaining;
    }

    private static Component nameOf(PatternProviderLogicHost host) {
        try {
            var group = host.getTerminalGroup();
            if (group != null && group.name() != null) {
                return group.name();
            }
        } catch (RuntimeException ignored) {
            // fall through to the generic name
        }
        return Component.translatable("ae2usefulutilities.pattern.picker.unnamed");
    }

    @Nullable
    private static ProviderKey keyOf(PatternProviderLogicHost host) {
        BlockEntity blockEntity = host.getBlockEntity();
        if (blockEntity == null) {
            return null;
        }
        Level level = blockEntity.getLevel();
        if (level == null) {
            return null;
        }
        BlockPos pos = blockEntity.getBlockPos();
        var side = host instanceof AEBasePart part ? part.getSide() : null;
        return ProviderKey.of(level.dimension().location(), pos, side);
    }

    @Nullable
    private static IGrid gridOf(ServerPlayer player) {
        if (!(player.containerMenu instanceof PatternEncodingTermMenu menu)) {
            return null;
        }
        IGridNode node = menu.getGridNode();
        return node == null ? null : node.getGrid();
    }

    private static void say(ServerPlayer player, ChatFormatting style, String key) {
        player.sendSystemMessage(Component.translatable(key).withStyle(style));
    }
}
