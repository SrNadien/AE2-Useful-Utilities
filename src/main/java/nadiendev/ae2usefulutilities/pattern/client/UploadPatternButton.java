package nadiendev.ae2usefulutilities.pattern.client;

import appeng.client.gui.widgets.IconButton;
import appeng.util.Icon;

/**
 * The counterpart of AE2's encode button: same size, sitting right next to it, pointing the other way.
 */
public class UploadPatternButton extends IconButton {

    public UploadPatternButton() {
        super(button -> ProviderPickerHooks.request());
    }

    @Override
    protected Icon getIcon() {
        return Icon.ARROW_UP;
    }
}
