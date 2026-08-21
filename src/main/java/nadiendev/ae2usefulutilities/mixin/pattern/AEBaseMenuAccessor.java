package nadiendev.ae2usefulutilities.mixin.pattern;

import appeng.menu.AEBaseMenu;
import appeng.menu.guisync.ClientActionKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AEBaseMenu.class)
public interface AEBaseMenuAccessor {

    @Invoker(value = "registerClientAction", remap = false)
    void ae2uu$registerClientAction(ClientActionKey<Void> key, Runnable callback);

    @Invoker(value = "sendClientAction", remap = false)
    void ae2uu$sendClientAction(ClientActionKey<Void> action);
}
