package me.neznamy.tab.platforms.forge;

import me.neznamy.tab.platforms.modded.ModdedTabPlayer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * TabPlayer implementation for Forge.
 */
public class ForgeTabPlayer extends ModdedTabPlayer {

    /**
     * Constructs new instance with given parameters.
     *
     * @param   platform
     *          Server platform
     * @param   player
     *          Platform's player object
     */
    public ForgeTabPlayer(@NotNull ForgePlatform platform, @NotNull ServerPlayer player) {
        super(platform, player);
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return true; // PermissionsAPIHook.hasPermission(getPlayer(), permission);
    }

    @Override
    public ForgePlatform getPlatform() {
        return (ForgePlatform) platform;
    }
}
