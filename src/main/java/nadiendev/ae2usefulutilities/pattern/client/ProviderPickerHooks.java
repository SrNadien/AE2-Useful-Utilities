package nadiendev.ae2usefulutilities.pattern.client;

import nadiendev.ae2usefulutilities.config.AE2UtilitiesConfig;
import nadiendev.ae2usefulutilities.pattern.ProviderEntry;
import nadiendev.ae2usefulutilities.pattern.network.RequestProvidersC2SPacket;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;

/**
 * Client half of the provider picker: asks the server for the provider list, then shows it on top of the terminal.
 */
public final class ProviderPickerHooks {

    private ProviderPickerHooks() {
    }

    public static void request() {
        if (!AE2UtilitiesConfig.PATTERN_UPLOADER_ENABLED.get()) {
            return;
        }
        ClientPacketDistributor.sendToServer(new RequestProvidersC2SPacket());
    }

    public static void open(List<ProviderEntry> entries) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        minecraft.setScreen(new ProviderSelectScreen(entries, minecraft.screen, ""));
    }
}
