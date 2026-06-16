package nadiendev.ae2usefulutilities.util;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import net.minecraft.world.item.ItemStack;

public class MENetworkHelper {

    public static ItemStack extractFromStorage(MEStorage storage, ItemStack target, int maxAmount) {
        if (storage == null || target.isEmpty()) return ItemStack.EMPTY;

        AEItemKey key = AEItemKey.of(target);
        if (key == null) return ItemStack.EMPTY;

        // Consultar disponibilidad
        KeyCounter available = new KeyCounter();
        storage.getAvailableStacks(available);
        long has = available.get(key);
        if (has <= 0) return ItemStack.EMPTY;

        long toExtract = Math.min(maxAmount, has);
        long extracted = storage.extract(key, toExtract, Actionable.MODULATE, IActionSource.empty());

        if (extracted <= 0) return ItemStack.EMPTY;
        return key.toStack((int) extracted);
    }
}