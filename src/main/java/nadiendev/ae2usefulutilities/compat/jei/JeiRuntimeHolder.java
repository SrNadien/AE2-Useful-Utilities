package nadiendev.ae2usefulutilities.compat.jei;

import mezz.jei.api.runtime.IJeiRuntime;
import org.jetbrains.annotations.Nullable;

public final class JeiRuntimeHolder {

    @Nullable
    private static IJeiRuntime runtime;

    private JeiRuntimeHolder() {
    }

    public static void set(@Nullable IJeiRuntime value) {
        runtime = value;
    }

    @Nullable
    public static IJeiRuntime get() {
        return runtime;
    }
}
