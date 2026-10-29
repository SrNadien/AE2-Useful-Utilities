package nadiendev.ae2usefulutilities.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class AE2UtilitiesClientConfig {

    public static final AE2UtilitiesClientConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public static ModConfigSpec.BooleanValue SHOW_NETWORK_STOCK;

    private AE2UtilitiesClientConfig(ModConfigSpec.Builder builder) {
        builder.push("jei");

        SHOW_NETWORK_STOCK = builder
                .comment(
                        "Show the amount stored in your ME network on the items of the JEI ingredient list.",
                        "Toggled with the terminal button next to JEI's history button."
                )
                .define("showNetworkStock", false);

        builder.pop();
    }

    static {
        Pair<AE2UtilitiesClientConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(AE2UtilitiesClientConfig::new);
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}
