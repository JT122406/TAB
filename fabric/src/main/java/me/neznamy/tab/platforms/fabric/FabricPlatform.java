package me.neznamy.tab.platforms.fabric;

import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import me.neznamy.tab.platforms.fabric.hook.FabricPermissionsAPIHook;
import me.neznamy.tab.platforms.fabric.hook.FabricTabExpansion;
import me.neznamy.tab.platforms.modded.*;
import me.neznamy.tab.platforms.modded.permissions.PermissionsAPIHook;
import me.neznamy.tab.shared.TAB;
import me.neznamy.tab.shared.TabConstants;
import me.neznamy.tab.shared.features.PerWorldPlayerListConfiguration;
import me.neznamy.tab.shared.features.PlaceholderManagerImpl;
import me.neznamy.tab.shared.features.types.TabFeature;
import me.neznamy.tab.shared.placeholders.expansion.EmptyTabExpansion;
import me.neznamy.tab.shared.placeholders.expansion.TabExpansion;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Platform implementation for Fabric
 */
public record FabricPlatform(MinecraftServer server, String modLoader) implements ModdedPlatform {
    @Override
    public void registerUnknownPlaceholder(@NotNull String identifier) {
        if (!FabricLoader.getInstance().isModLoaded("placeholder-api")) {
            registerDummyPlaceholder(identifier);
            return;
        }

        PlaceholderManagerImpl manager = TAB.getInstance().getPlaceholderManager();
        manager.registerPlayerPlaceholder(identifier,
                p -> Placeholders.parseText(
                        Component.literal(identifier),
                        PlaceholderContext.of((ServerPlayer) p.getPlayer())
                ).getString()
        );
    }

    @Override
    @NotNull
    public TabExpansion createTabExpansion() {
        if (FabricLoader.getInstance().isModLoaded("placeholder-api"))
            return new FabricTabExpansion();
        return new EmptyTabExpansion();
    }

    @Override
    @Nullable
    public TabFeature getPerWorldPlayerList(@NotNull PerWorldPlayerListConfiguration configuration) {
        return null;
    }

    @Override
    public void registerListener() {
        new FabricEventListener().register();
    }

    @Override
    @NotNull
    public File getDataFolder() {
        return FabricLoader.getInstance().getConfigDir().resolve(TabConstants.PLUGIN_ID).toFile();
    }

    @Override
    public ModdedTabPlayer getPlayer(ModdedPlatform platform, ServerPlayer player) {
        return new FabricTabPlayer(this, player);
    }

    @Override
    public PermissionsAPIHook getPermissionsHook() {
        if (FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0"))
            return new FabricPermissionsAPIHook();
        return ModdedPlatform.super.getPermissionsHook();
    }
}
