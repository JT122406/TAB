package me.neznamy.tab.platforms.neoforge;

import me.neznamy.tab.shared.TAB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

@Mod(value = "tab", dist = Dist.DEDICATED_SERVER)
public class NeoForgeTAB {

	public NeoForgeTAB(final IEventBus eventBus) {
		IEventBus EVENT_BUS = NeoForge.EVENT_BUS;
		EVENT_BUS.addListener((RegisterCommandsEvent event) -> new NeoForgeTabCommand().onRegisterCommands(event.getDispatcher()));
		EVENT_BUS.addListener((ServerStartingEvent event) -> TAB.create(new NeoForgePlatform(event.getServer())));
		EVENT_BUS.addListener((ServerStoppingEvent event) -> TAB.getInstance().unload());
	}
}
