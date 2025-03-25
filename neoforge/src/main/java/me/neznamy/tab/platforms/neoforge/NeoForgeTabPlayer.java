package me.neznamy.tab.platforms.neoforge;

import me.neznamy.chat.component.TabComponent;
import me.neznamy.tab.platforms.modded.ModdedTabPlayer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * TabPlayer implementation for NeoForge.
 */
public class NeoForgeTabPlayer extends ModdedTabPlayer {

    /**
     * Constructs new instance with given parameters.
     *
     * @param   platform
     *          Server platform
     * @param   player
     *          Platform's player object
     */
    public NeoForgeTabPlayer(@NotNull NeoForgePlatform platform, @NotNull ServerPlayer player) {
        super(platform, player);
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return true; // PermissionsAPIHook.hasPermission(getPlayer(), permission);
    }

    @Override
    public void sendMessage(@NotNull TabComponent message) {
        getPlayer().sendSystemMessage(message.convert());
    }

    @Override
    public NeoForgePlatform getPlatform() {
        return (NeoForgePlatform) platform;
    }
}
