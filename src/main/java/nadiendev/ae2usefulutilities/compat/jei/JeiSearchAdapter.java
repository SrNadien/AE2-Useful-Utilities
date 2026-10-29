package nadiendev.ae2usefulutilities.compat.jei;

import appeng.integration.abstraction.ItemListModAdapter;
import mezz.jei.api.runtime.IJeiRuntime;

public final class JeiSearchAdapter implements ItemListModAdapter {

    private final IJeiRuntime runtime;

    public JeiSearchAdapter(IJeiRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getShortName() {
        return "JEI";
    }

    @Override
    public String getSearchText() {
        String text = runtime.getIngredientFilter().getFilterText();
        return text == null ? "" : text;
    }

    @Override
    public void setSearchText(String text) {
        runtime.getIngredientFilter().setFilterText(text);
    }

    @Override
    public boolean hasSearchFocus() {
        return runtime.getIngredientListOverlay().hasKeyboardFocus();
    }
}
