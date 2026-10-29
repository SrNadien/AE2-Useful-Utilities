package nadiendev.ae2usefulutilities.stock.server;

import appeng.api.networking.IGrid;
import appeng.api.stacks.AEKey;
import nadiendev.ae2usefulutilities.config.AE2UtilitiesConfig;
import nadiendev.ae2usefulutilities.stock.network.NetworkStockS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class NetworkStockService {

    private static final int ENTRIES_PER_PACKET = 600;
    private static final Map<UUID, Snapshot> SNAPSHOTS = new HashMap<>();

    private static final class Snapshot {
        private final Map<AEKey, Long> amounts = new HashMap<>();
        private final Set<AEKey> craftables = new HashSet<>();
        private int lastSyncTick = Integer.MIN_VALUE;
    }

    private NetworkStockService() {
    }

    public static void sync(ServerPlayer player, boolean full) {
        if (!AE2UtilitiesConfig.NETWORK_STOCK_ENABLED.get()) {
            return;
        }
        IGrid grid = gridOf(player);
        if (grid == null) {
            if (SNAPSHOTS.remove(player.getUUID()) != null || full) {
                PacketDistributor.sendToPlayer(player, new NetworkStockS2CPacket(false, true, List.of()));
            }
            return;
        }

        Snapshot snapshot = SNAPSHOTS.get(player.getUUID());
        boolean fresh = full || snapshot == null;
        if (!fresh && player.tickCount - snapshot.lastSyncTick < AE2UtilitiesConfig.NETWORK_STOCK_SYNC_TICKS.get()) {
            return;
        }
        if (fresh) {
            snapshot = new Snapshot();
            SNAPSHOTS.put(player.getUUID(), snapshot);
        }
        snapshot.lastSyncTick = player.tickCount;

        Map<AEKey, Long> amounts = new HashMap<>();
        for (var entry : grid.getStorageService().getInventory().getAvailableStacks()) {
            amounts.put(entry.getKey(), entry.getLongValue());
        }
        Set<AEKey> craftables = grid.getCraftingService().getCraftables(key -> true);

        Set<AEKey> keys = new HashSet<>(amounts.keySet());
        keys.addAll(craftables);

        List<NetworkStockS2CPacket.Entry> entries = new ArrayList<>();
        for (AEKey key : keys) {
            long amount = amounts.getOrDefault(key, 0L);
            boolean craftable = craftables.contains(key);
            Long known = snapshot.amounts.get(key);
            if (known == null || known != amount || snapshot.craftables.contains(key) != craftable) {
                entries.add(new NetworkStockS2CPacket.Entry(key, amount, craftable));
            }
        }
        for (AEKey key : snapshot.amounts.keySet()) {
            if (!keys.contains(key)) {
                entries.add(new NetworkStockS2CPacket.Entry(key, 0, false));
            }
        }

        snapshot.amounts.clear();
        for (AEKey key : keys) {
            snapshot.amounts.put(key, amounts.getOrDefault(key, 0L));
        }
        snapshot.craftables.clear();
        snapshot.craftables.addAll(craftables);

        send(player, fresh, entries);
    }

    private static void send(ServerPlayer player, boolean reset, List<NetworkStockS2CPacket.Entry> entries) {
        if (entries.isEmpty()) {
            if (reset) {
                PacketDistributor.sendToPlayer(player, new NetworkStockS2CPacket(true, true, List.of()));
            }
            return;
        }
        for (int start = 0; start < entries.size(); start += ENTRIES_PER_PACKET) {
            int end = Math.min(start + ENTRIES_PER_PACKET, entries.size());
            PacketDistributor.sendToPlayer(player, new NetworkStockS2CPacket(true, reset && start == 0,
                    List.copyOf(entries.subList(start, end))));
        }
    }

    @Nullable
    private static IGrid gridOf(ServerPlayer player) {
        TerminalAccess access = TerminalLocator.find(player);
        return access == null ? null : access.grid();
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        SNAPSHOTS.remove(event.getEntity().getUUID());
    }
}
