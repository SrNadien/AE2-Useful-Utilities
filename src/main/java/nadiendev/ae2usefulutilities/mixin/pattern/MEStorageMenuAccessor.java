package nadiendev.ae2usefulutilities.mixin.pattern;

import appeng.api.storage.MEStorage;
import appeng.menu.me.common.MEStorageMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MEStorageMenu.class)
public interface MEStorageMenuAccessor {

    @Accessor(value = "storage", remap = false)
    MEStorage ae2uu$getStorage();
}
