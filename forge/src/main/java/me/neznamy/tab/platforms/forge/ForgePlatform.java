package me.neznamy.tab.platforms.forge;

import me.neznamy.tab.platforms.modded.*;
import me.neznamy.tab.shared.TabConstants;
import me.neznamy.tab.shared.features.PerWorldPlayerListConfiguration;
import me.neznamy.tab.shared.features.types.TabFeature;
import me.neznamy.tab.shared.placeholders.expansion.EmptyTabExpansion;
import me.neznamy.tab.shared.placeholders.expansion.TabExpansion;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Platform implementation for Forge
 */
public record ForgePlatform(MinecraftServer server, String modLoader) implements ModdedPlatform {
    @Override
    public void registerUnknownPlaceholder(@NotNull String identifier) {
        registerDummyPlaceholder(identifier);
    }

    @Override
    @NotNull
    public TabExpansion createTabExpansion() {
        return new EmptyTabExpansion();
    }

    @Override
    @Nullable
    public TabFeature getPerWorldPlayerList(@NotNull PerWorldPlayerListConfiguration configuration) {
        return null;
    }

    @Override
    public void registerListener() {
        new ForgeEventListener().register();
    }

    @Override
    @NotNull
    public File getDataFolder() {
        return FMLPaths.CONFIGDIR.get().resolve(TabConstants.PLUGIN_ID).toFile();
    }
}
