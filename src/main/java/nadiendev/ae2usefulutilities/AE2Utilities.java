package nadiendev.ae2usefulutilities;

import nadiendev.ae2usefulutilities.config.AE2UtilitiesClientConfig;
import nadiendev.ae2usefulutilities.config.AE2UtilitiesConfig;
import nadiendev.ae2usefulutilities.handler.ClientPickBlockHandler;
import nadiendev.ae2usefulutilities.handler.PickBlockHandler;
import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import nadiendev.ae2usefulutilities.stock.client.NetworkStockClient;
import nadiendev.ae2usefulutilities.stock.server.NetworkStockService;
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
        container.registerConfig(ModConfig.Type.CLIENT, AE2UtilitiesClientConfig.CONFIG_SPEC);

        modBus.addListener(PatternNetwork::register);
        NeoForge.EVENT_BUS.register(NetworkStockService.class);

        PickBlockHandler handler = new PickBlockHandler();
        modBus.register(handler);

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            NeoForge.EVENT_BUS.register(new ClientPickBlockHandler());
            NeoForge.EVENT_BUS.register(new NetworkStockClient());
        }
    }
}
