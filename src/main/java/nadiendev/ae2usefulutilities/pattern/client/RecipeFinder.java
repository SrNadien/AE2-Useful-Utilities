package nadiendev.ae2usefulutilities.pattern.client;

import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

public interface RecipeFinder {

    @Nullable
    Component getWorkstationName(String categoryId);

    @Nullable
    static RecipeFinder get() {
        return Holder.INSTANCE;
    }

    final class Holder {
        static final boolean JEI = ModList.get().isLoaded("jei");
        @Nullable
        static final RecipeFinder INSTANCE = JEI ? new RecipeFinderJEI() : null;

        private Holder() {
        }
    }
}
