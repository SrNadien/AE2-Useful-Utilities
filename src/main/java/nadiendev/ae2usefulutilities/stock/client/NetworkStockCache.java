package nadiendev.ae2usefulutilities.stock.client;

import appeng.api.stacks.AEKey;
import nadiendev.ae2usefulutilities.stock.network.NetworkStockS2CPacket;

import java.util.HashMap;
import java.util.Map;

public final class NetworkStockCache {

    private record Stock(long amount, boolean craftable) {
    }

    private static final Map<AEKey, Stock> ENTRIES = new HashMap<>();
    private static boolean connected;

    private NetworkStockCache() {
    }

    public static void apply(NetworkStockS2CPacket packet) {
        if (packet.reset() || !packet.connected()) {
            ENTRIES.clear();
        }
        connected = packet.connected();
        if (!connected) {
            return;
        }
        for (NetworkStockS2CPacket.Entry entry : packet.entries()) {
            if (entry.isRemoval()) {
                ENTRIES.remove(entry.key());
            } else {
                ENTRIES.put(entry.key(), new Stock(entry.amount(), entry.craftable()));
            }
        }
    }

    public static void clear() {
        ENTRIES.clear();
        connected = false;
    }

    public static boolean isConnected() {
        return connected;
    }

    public static long amount(AEKey key) {
        Stock stock = ENTRIES.get(key);
        return stock == null ? 0 : stock.amount();
    }

    public static boolean isCraftable(AEKey key) {
        Stock stock = ENTRIES.get(key);
        return stock != null && stock.craftable();
    }
}
