package nadiendev.ae2usefulutilities.mixin.pattern;

import appeng.menu.me.items.PatternEncodingTermMenu;
import nadiendev.ae2usefulutilities.config.AE2UtilitiesConfig;
import nadiendev.ae2usefulutilities.pattern.client.ProviderPickerHooks;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PatternEncodingTermMenu.class)
public abstract class PatternEncodingTermMenuClientMixin {

    @Inject(method = "encode", at = @At("HEAD"), remap = false, cancellable = true)
    private void ae2uu$uploadInsteadOfEncode(CallbackInfo ci) {
        PatternEncodingTermMenu self = (PatternEncodingTermMenu) (Object) this;
        if (!self.isClientSide() || !AE2UtilitiesConfig.PATTERN_UPLOADER_ENABLED.get()) {
            return;
        }
        if (Screen.hasAltDown()) {
            ProviderPickerHooks.request();
            ci.cancel();
        }
    }
}
