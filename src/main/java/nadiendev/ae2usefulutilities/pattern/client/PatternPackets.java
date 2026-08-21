package nadiendev.ae2usefulutilities.pattern.client;

import nadiendev.ae2usefulutilities.config.AE2UtilitiesConfig;
import nadiendev.ae2usefulutilities.pattern.network.EncodeWithCategoryIdC2SPacket;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;

public final class PatternPackets {

    private PatternPackets() {
    }

    public static void sendCategory(ResourceLocation categoryId) {
        if (categoryId == null || !AE2UtilitiesConfig.PATTERN_UPLOADER_ENABLED.get()) {
            return;
        }
        PacketDistributor.sendToServer(new EncodeWithCategoryIdC2SPacket(categoryId));
    }
}
