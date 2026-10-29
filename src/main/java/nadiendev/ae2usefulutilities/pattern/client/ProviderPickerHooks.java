package nadiendev.ae2usefulutilities.pattern.client;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.menu.me.items.PatternEncodingTermMenu;
import nadiendev.ae2usefulutilities.config.AE2UtilitiesConfig;
import nadiendev.ae2usefulutilities.pattern.ProviderEntry;
import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import nadiendev.ae2usefulutilities.pattern.network.RequestProvidersC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;

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
        minecraft.setScreen(new ProviderSelectScreen(entries, minecraft.screen, guessFilter()));
    }

    private static String guessFilter() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        RecipeFinder finder = RecipeFinder.get();
        if (player == null || finder == null || !(player.containerMenu instanceof PatternEncodingTermMenu)) {
            return "";
        }
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getNonEquipmentItems().size(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.isEmpty() || !PatternDetailsHelper.isEncodedPattern(stack)) {
                continue;
            }
            CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag tag = data.copyTag();
            if (!tag.contains(PatternNetwork.CATEGORY_TAG)) {
                continue;
            }
            Component name = finder.getWorkstationName(tag.getStringOr(PatternNetwork.CATEGORY_TAG, ""));
            if (name != null) {
                return name.getString();
            }
        }
        return "";
    }
}
