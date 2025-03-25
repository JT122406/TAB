package me.neznamy.tab.platforms.forge;

import me.neznamy.tab.platforms.modded.*;
import me.neznamy.tab.shared.TabConstants;
import me.neznamy.tab.shared.features.PerWorldPlayerListConfiguration;
import me.neznamy.tab.shared.features.types.TabFeature;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
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

    @Override
    public ModdedTabPlayer getPlayer(ModdedPlatform platform, ServerPlayer player) {
        return new ForgeTabPlayer(this, player);
    }
}
