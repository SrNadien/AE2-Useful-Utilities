package nadiendev.ae2usefulutilities.stock.server;

import appeng.api.networking.IGrid;
import appeng.menu.locator.MenuHostLocator;
import org.jetbrains.annotations.Nullable;

public record TerminalAccess(IGrid grid, @Nullable MenuHostLocator locator) {
}
