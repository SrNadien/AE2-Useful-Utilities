package nadiendev.ae2usefulutilities.mixin.pattern;

import appeng.api.config.ActionItems;
import appeng.client.gui.me.items.PatternEncodingTermScreen;
import appeng.client.gui.widgets.ActionButton;
import appeng.client.gui.widgets.IconButton;
import appeng.menu.SlotSemantics;
import appeng.menu.me.items.PatternEncodingTermMenu;
import nadiendev.ae2usefulutilities.config.AE2UtilitiesConfig;
import nadiendev.ae2usefulutilities.pattern.client.UploadPatternButton;
import nadiendev.ae2usefulutilities.pattern.network.PatternNetwork;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PatternEncodingTermScreen.class)
public abstract class PatternEncodingTermScreenMixin {

    @Unique
    private ActionButton ae2uu$returnButton;

    @Unique
    private UploadPatternButton ae2uu$uploadButton;

    @Inject(method = "updateBeforeRender", at = @At("HEAD"), remap = false)
    private void ae2uu$addButtons(CallbackInfo ci) {
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;
        if (!(screen.getMenu() instanceof PatternEncodingTermMenu menu)) {
            return;
        }

        List<Slot> slots = menu.getSlots(SlotSemantics.ENCODED_PATTERN);
        if (slots.isEmpty()) {
            return;
        }
        Slot encodedSlot = slots.getFirst();

        if (this.ae2uu$returnButton == null) {
            this.ae2uu$returnButton = new ActionButton(ActionItems.S_CLOSE,
                    () -> ((AEBaseMenuAccessor) menu).ae2uu$sendClientAction(PatternNetwork.RETURN_ACTION));
            this.ae2uu$returnButton.setHalfSize(true);
            this.ae2uu$returnButton.setDisableBackground(true);
            ae2uu$describe(this.ae2uu$returnButton, "ae2usefulutilities.pattern.button.return");
        }
        this.ae2uu$returnButton.setX(screen.getGuiLeft() + encodedSlot.x + 14);
        this.ae2uu$returnButton.setY(screen.getGuiTop() + encodedSlot.y - 6);
        this.ae2uu$returnButton.setVisibility(
                AE2UtilitiesConfig.PATTERN_RETURN_BUTTON.get() && !encodedSlot.getItem().isEmpty());

        if (this.ae2uu$uploadButton == null) {
            this.ae2uu$uploadButton = new UploadPatternButton();
            ae2uu$describe(this.ae2uu$uploadButton, "ae2usefulutilities.pattern.button.upload");
        }
        // Immediately left of AE2's encode button, which sits 27px above the encoded pattern slot.
        this.ae2uu$uploadButton.setX(screen.getGuiLeft() + encodedSlot.x - 19);
        this.ae2uu$uploadButton.setY(screen.getGuiTop() + encodedSlot.y - 28);
        this.ae2uu$uploadButton.setVisibility(AE2UtilitiesConfig.PATTERN_UPLOADER_ENABLED.get());

        ae2uu$ensureAdded(this.ae2uu$returnButton);
        ae2uu$ensureAdded(this.ae2uu$uploadButton);
    }

    @Unique
    private void ae2uu$describe(IconButton button, String translationKey) {
        button.setMessage(Component.translatable(translationKey)
                .append("\n")
                .append(Component.translatable(translationKey + ".desc")));
    }

    @Unique
    private void ae2uu$ensureAdded(IconButton button) {
        ScreenAccessor accessor = (ScreenAccessor) (Object) this;
        if (!accessor.ae2uu$getRenderables().contains(button)) {
            accessor.ae2uu$addRenderableWidget(button);
        }
    }
}
