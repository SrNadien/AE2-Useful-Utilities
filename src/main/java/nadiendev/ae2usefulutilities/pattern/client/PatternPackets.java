package nadiendev.ae2usefulutilities.pattern.client;

import nadiendev.ae2usefulutilities.config.AE2UtilitiesConfig;
import nadiendev.ae2usefulutilities.pattern.network.EncodeWithCategoryIdC2SPacket;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public final class PatternPackets {

    private PatternPackets() {
    }

    public static void sendCategory(Identifier categoryId) {
        if (categoryId == null || !AE2UtilitiesConfig.PATTERN_UPLOADER_ENABLED.get()) {
            return;
        }
        ClientPacketDistributor.sendToServer(new EncodeWithCategoryIdC2SPacket(categoryId));
    }
}
