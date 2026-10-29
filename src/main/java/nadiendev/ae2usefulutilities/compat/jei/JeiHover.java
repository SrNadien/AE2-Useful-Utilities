package nadiendev.ae2usefulutilities.compat.jei;

import appeng.api.stacks.AEKey;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IJeiRuntime;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class JeiHover {

    private JeiHover() {
    }

    @Nullable
    public static AEKey key() {
        IJeiRuntime runtime = JeiRuntimeHolder.get();
        if (runtime == null) {
            return null;
        }
        Optional<ITypedIngredient<?>> listed = runtime.getIngredientListOverlay().getIngredientUnderMouse();
        if (listed.isPresent()) {
            return JeiKeys.keyOf(listed.get());
        }
        Optional<ITypedIngredient<?>> bookmarked = runtime.getBookmarkOverlay().getIngredientUnderMouse();
        return bookmarked.map(JeiKeys::keyOf).orElse(null);
    }
}
