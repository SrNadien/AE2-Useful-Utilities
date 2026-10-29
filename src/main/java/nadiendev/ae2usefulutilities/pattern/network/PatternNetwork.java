package nadiendev.ae2usefulutilities.pattern.network;

import appeng.menu.guisync.ClientActionKey;
import nadiendev.ae2usefulutilities.stock.network.NetworkStockRequestC2SPacket;
import nadiendev.ae2usefulutilities.stock.network.NetworkStockS2CPacket;
import nadiendev.ae2usefulutilities.stock.network.OpenCraftC2SPacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class PatternNetwork {

    public static final String MODID = "ae2usefulutilities";
    public static final String CATEGORY_TAG = "ae2uu_categoryId";
    public static final ClientActionKey<Void> RETURN_ACTION = new ClientActionKey<>("ae2uu_returnPattern");

    private PatternNetwork() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1").optional();
        registrar.playToServer(EncodeWithCategoryIdC2SPacket.TYPE,
                EncodeWithCategoryIdC2SPacket.STREAM_CODEC,
                EncodeWithCategoryIdC2SPacket::handle);
        registrar.playToServer(RequestProvidersC2SPacket.TYPE,
                RequestProvidersC2SPacket.STREAM_CODEC,
                RequestProvidersC2SPacket::handle);
        registrar.playToServer(UploadToProviderC2SPacket.TYPE,
                UploadToProviderC2SPacket.STREAM_CODEC,
                UploadToProviderC2SPacket::handle);
        registrar.playToClient(ProviderListS2CPacket.TYPE,
                ProviderListS2CPacket.STREAM_CODEC,
                ProviderListS2CPacket::handle);
        registrar.playToServer(NetworkStockRequestC2SPacket.TYPE,
                NetworkStockRequestC2SPacket.STREAM_CODEC,
                NetworkStockRequestC2SPacket::handle);
        registrar.playToServer(OpenCraftC2SPacket.TYPE,
                OpenCraftC2SPacket.STREAM_CODEC,
                OpenCraftC2SPacket::handle);
        registrar.playToClient(NetworkStockS2CPacket.TYPE,
                NetworkStockS2CPacket.STREAM_CODEC,
                NetworkStockS2CPacket::handle);
    }
}
