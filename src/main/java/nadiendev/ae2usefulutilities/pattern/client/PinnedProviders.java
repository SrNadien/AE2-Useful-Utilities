package nadiendev.ae2usefulutilities.pattern.client;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

public final class PinnedProviders {

    private static final Gson GSON = new Gson();
    private static final Set<String> PINNED = new LinkedHashSet<>();
    private static boolean loaded;

    private PinnedProviders() {
    }

    public static synchronized boolean isPinned(String name) {
        load();
        return PINNED.contains(name);
    }

    public static synchronized boolean toggle(String name) {
        load();
        boolean nowPinned = PINNED.add(name);
        if (!nowPinned) {
            PINNED.remove(name);
        }
        save();
        return nowPinned;
    }

    private static Path file() {
        return FMLPaths.CONFIGDIR.get().resolve("ae2usefulutilities").resolve("pinned_providers.json");
    }

    private static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        Path file = file();
        if (!Files.exists(file)) {
            return;
        }
        try {
            String[] names = GSON.fromJson(Files.readString(file), String[].class);
            if (names != null) {
                for (String name : names) {
                    if (name != null && !name.isBlank()) {
                        PINNED.add(name);
                    }
                }
            }
        } catch (IOException | JsonParseException ignored) {
            PINNED.clear();
        }
    }

    private static void save() {
        Path file = file();
        try {
            Files.createDirectories(file.getParent());
            Files.writeString(file, GSON.toJson(PINNED.toArray(new String[0])));
        } catch (IOException ignored) {
        }
    }
}
