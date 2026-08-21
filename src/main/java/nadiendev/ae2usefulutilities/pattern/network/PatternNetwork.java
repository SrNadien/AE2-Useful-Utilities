package nadiendev.ae2usefulutilities.pattern.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class PatternNetwork {

    public static final String MODID = "ae2usefulutilities";
    public static final String CATEGORY_TAG = "ae2uu_categoryId";
    public static final String RETURN_ACTION = "ae2uu_returnPattern";

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
    }
}
