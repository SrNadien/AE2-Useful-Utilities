package nadiendev.ae2usefulutilities;

import nadiendev.ae2usefulutilities.config.AE2UtilitiesConfig;
import nadiendev.ae2usefulutilities.handler.ClientPickBlockHandler;
import nadiendev.ae2usefulutilities.handler.PickBlockHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod("ae2usefulutilities")
public class AE2Utilities {

    public AE2Utilities(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, AE2UtilitiesConfig.CONFIG_SPEC);

        PickBlockHandler handler = new PickBlockHandler();
        modBus.register(handler);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            NeoForge.EVENT_BUS.register(new ClientPickBlockHandler());
        }
    }
}