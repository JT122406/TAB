package me.neznamy.tab.platforms.fabric;

import me.neznamy.tab.platforms.modded.ModdedTabPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * TabPlayer implementation for Fabric.
 */
public class FabricTabPlayer extends ModdedTabPlayer {

    /**
     * Constructs new instance with given parameters.
     *
     * @param   platform
     *          Server platform
     * @param   player
     *          Platform's player object
     */
    public FabricTabPlayer(@NotNull FabricPlatform platform, @NotNull ServerPlayer player) {
        super(platform, player);
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return getPlatform().getPermissionsHook().hasPermission(getPlayer().createCommandSourceStack(), permission);
    }

    @Override
    public boolean hasPermission(CommandSourceStack sourceStack, String permission) {
        return getPlatform().getPermissionsHook().hasPermission(sourceStack, permission);
    }

    @Override
    public FabricPlatform getPlatform() {
        return (FabricPlatform) platform;
    }
}
