package me.neznamy.tab.platforms.modded;

import com.mojang.logging.LogUtils;
import me.neznamy.chat.component.KeybindComponent;
import me.neznamy.chat.component.TabComponent;
import me.neznamy.chat.component.TextComponent;
import me.neznamy.chat.component.TranslatableComponent;
import me.neznamy.tab.platforms.modded.permissions.PermissionsAPIHook;
import me.neznamy.tab.shared.TAB;
import me.neznamy.tab.shared.backend.BackendPlatform;
import me.neznamy.tab.shared.features.injection.PipelineInjector;
import me.neznamy.tab.shared.platform.BossBar;
import me.neznamy.tab.shared.platform.Scoreboard;
import me.neznamy.tab.shared.platform.TabList;
import me.neznamy.tab.shared.platform.TabPlayer;
import net.minecraft.SharedConstants;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * Platform implementation for Modloaders
 */
public interface ModdedPlatform extends BackendPlatform {

    MinecraftServer server();

    String modLoader();

    @Override
    default void loadPlayers() {
        for (ServerPlayer player : getOnlinePlayers()) {
            TAB.getInstance().addPlayer(new ModdedTabPlayer(this, player));
        }
    }

    private Collection<ServerPlayer> getOnlinePlayers() {
        // It's nullable on startup
        return server().getPlayerList() == null ? Collections.emptyList() : server().getPlayerList().getPlayers();
    }

    @Override
    default @NotNull Scoreboard createScoreboard(@NotNull TabPlayer player) {
        return new ModdedScoreboard((ModdedTabPlayer) player);
    }

    @Override
    default @NotNull BossBar createBossBar(@NotNull TabPlayer player) {
        return new ModdedBossBar((ModdedTabPlayer) player);
    }

    @Override
    default @NotNull TabList createTabList(@NotNull TabPlayer player) {
        return new ModdedTabList((ModdedTabPlayer) player);
    }

    @Override
    default @Nullable PipelineInjector createPipelineInjector() {
        return new ModdedPipelineInjector();
    }

    @Override
    default void logInfo(@NotNull TabComponent message) {
        LogUtils.getLogger().info("[TAB] {}", message.toRawText());
    }

    @Override
    default void logWarn(@NotNull TabComponent message) {
        LogUtils.getLogger().warn("[TAB] {}", message.toRawText());
    }

    @Override
    default boolean supportsScoreboards() {
        return true;
    }

    @Override
    default double getTPS() {
        double mspt = getMSPT();
        if (mspt < 50) return 20;
        return Math.round(1000 / mspt);
    }

    @Override
    default double getMSPT() {
        return (double) server().getAverageTickTimeNanos() / 1000000;
    }

    @Override
    @NotNull
    default Component convertComponent(@NotNull TabComponent component) {
        // Component type
        MutableComponent nmsComponent;
        if (component instanceof TextComponent) {
            nmsComponent = Component.literal(((TextComponent) component).getText());
        } else if (component instanceof TranslatableComponent) {
            nmsComponent = Component.translatable(((TranslatableComponent) component).getKey());
        } else if (component instanceof KeybindComponent) {
            nmsComponent = Component.keybind(((KeybindComponent) component).getKeybind());
        } else {
            throw new IllegalStateException("Unexpected component type: " + component.getClass().getName());
        }

        // Component style
        nmsComponent.setStyle(new Style(
                component.getModifier().getColor() == null ? null : TextColor.fromRgb(component.getModifier().getColor().getRgb()),
                component.getModifier().getShadowColor(),
                component.getModifier().getBold(),
                component.getModifier().getItalic(),
                component.getModifier().getUnderlined(),
                component.getModifier().getStrikethrough(),
                component.getModifier().getObfuscated(),
                null,
                null,
                null,
                component.getModifier().getFont() == null ? null : ResourceLocation.tryParse(component.getModifier().getFont())
        ));

        // Extra
        for (TabComponent extra : component.getExtra()) {
            nmsComponent.getSiblings().add(convertComponent(extra));
        }

        return nmsComponent;
    }

    @Override
    default String getServerVersionInfo() {
        return "[" + modLoader() + "] " + SharedConstants.getCurrentVersion().getName();
    }

    @Override
    default void registerCommand() {}

    @Override
    default void startMetrics() {}

    default PermissionsAPIHook getPermissionsHook() {
        return new PermissionsAPIHook() {
            @Override
            public boolean hasPermission(@NotNull CommandSourceStack sourceStack, @NotNull String permission) {
                return PermissionsAPIHook.super.hasPermission(sourceStack, permission);
            }
        };
    }
}
