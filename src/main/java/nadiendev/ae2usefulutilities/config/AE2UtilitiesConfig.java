package nadiendev.ae2usefulutilities.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class AE2UtilitiesConfig {

    public static final AE2UtilitiesConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public static ModConfigSpec.BooleanValue INFINITY_BOOSTER_CHUNK_LOADING;

    private AE2UtilitiesConfig(ModConfigSpec.Builder builder) {
        builder.push("compat");
        builder.push("aeinfinitybooster");

        INFINITY_BOOSTER_CHUNK_LOADING = builder
                .comment(
                        "If true and AEInfinityBooster is installed, inserting a Dimension Card or",
                        "Infinity Card into a Wireless Access Point will keep the chunk loaded.",
                        "Requires AEInfinityBooster to be present, otherwise has no effect."
                )
                .define("chunkLoadingEnabled", true);

        builder.pop();
        builder.pop();
    }

    static {
        Pair<AE2UtilitiesConfig, ModConfigSpec> pair =
                new ModConfigSpec.Builder().configure(AE2UtilitiesConfig::new);
        CONFIG = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }
}