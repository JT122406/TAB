package me.neznamy.tab.platforms.modded;

import me.neznamy.chat.component.TabComponent;
import me.neznamy.tab.shared.backend.BackendPlatform;
import me.neznamy.tab.shared.backend.BackendTabPlayer;
import me.neznamy.tab.shared.platform.Platform;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class ModdedTabPlayer extends BackendTabPlayer {
    /**
     * Constructs new instance with given parameters
     *
     * @param platform      Server platform reference
     * @param player        platform-specific player object
     */
    protected ModdedTabPlayer(@NotNull BackendPlatform platform, @NotNull ServerPlayer player) {
        super(platform, player, player.getUUID(), player.getGameProfile().getName(), LevelNameGetter.getLevelName(player.serverLevel()), SharedConstants.getProtocolVersion());
    }

    @Override
    public double getHealth() {
        return getPlayer().getHealth();
    }

    @Override
    public String getDisplayName() {
        return getPlayer().getGameProfile().getName();
    }

    @Override
    public boolean isVanished0() {
        return false;
    }

    @Override
    public boolean isDisguised() {
        return false;
    }

    @Override
    public boolean hasInvisibilityPotion() {
        return false;
    }

    @Override
    public int getGamemode() {
        return getPlayer().gameMode.getGameModeForPlayer().getId();
    }

    @Override
    public int getPing() {
        return getPlayer().connection.latency();
    }

    @Override
    public void sendMessage(@NotNull TabComponent message) {
        getPlayer().sendSystemMessage(message.convert());
    }

    @Override
    public boolean hasPermission(@NotNull String permission) {
        return false;
    }

    public boolean hasPermission(CommandSourceStack sourceStack, String permission) {
        return hasPermission(permission);
    }

    @Override
    public Platform getPlatform() {
        return null;
    }

    @Override
    public @NotNull ServerPlayer getPlayer() {
        return (ServerPlayer) player;
    }
}
