package nadiendev.ae2usefulutilities.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class AE2UtilitiesConfig {

    public static final AE2UtilitiesConfig CONFIG;
    public static final ModConfigSpec CONFIG_SPEC;

    public static ModConfigSpec.BooleanValue INFINITY_BOOSTER_CHUNK_LOADING;

    public static ModConfigSpec.BooleanValue PATTERN_UPLOADER_ENABLED;
    public static ModConfigSpec.BooleanValue PATTERN_RESTOCK_BLANKS;
    public static ModConfigSpec.BooleanValue PATTERN_RETURN_BUTTON;

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

        builder.push("pattern");
        builder.push("uploader");

        PATTERN_UPLOADER_ENABLED = builder
                .comment(
                        "Adds an upload button next to Encode in the Pattern Encoding Terminal. It lists every",
                        "Pattern Provider on the network and sends the patterns you carry to the one you pick.",
                        "ALT + Encode does the same."
                )
                .define("enabled", true);

        builder.pop();
        builder.push("restocker");

        PATTERN_RESTOCK_BLANKS = builder
                .comment("Pull Blank Patterns from ME storage when encoding with an empty blank pattern slot.")
                .define("autoRestock", true);

        PATTERN_RETURN_BUTTON = builder
                .comment("Show a button on the Pattern Encoding Terminal that turns the encoded pattern back to blank.")
                .define("returnButton", true);

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