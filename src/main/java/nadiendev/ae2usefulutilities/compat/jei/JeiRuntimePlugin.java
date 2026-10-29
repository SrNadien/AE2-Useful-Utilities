package nadiendev.ae2usefulutilities.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import net.minecraft.resources.Identifier;

@JeiPlugin
public final class JeiRuntimePlugin implements IModPlugin {

    private static final Identifier ID = Identifier.fromNamespaceAndPath(PatternNetwork.MODID, "jei_runtime");

    @Override
    public Identifier getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        JeiRuntimeHolder.set(runtime);
    }

    @Override
    public void onRuntimeUnavailable() {
        JeiRuntimeHolder.set(null);
    }
}
