package me.neznamy.tab.platforms.modded;

import io.netty.channel.Channel;
import me.neznamy.tab.shared.features.injection.NettyPipelineInjector;
import me.neznamy.tab.shared.platform.TabPlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Pipeline injector for Modded Platforms.
 */
public class ModdedPipelineInjector extends NettyPipelineInjector {

    /**
     * Constructs new instance.
     */
    public ModdedPipelineInjector() {
        super("packet_handler");
    }

    @Override
    @NotNull
    protected Channel getChannel(@NotNull TabPlayer player) {
        return ((ModdedTabPlayer)player).getPlayer().connection.connection.channel;
    }
}
