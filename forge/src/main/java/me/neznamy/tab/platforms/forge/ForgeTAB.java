package me.neznamy.tab.platforms.forge;

import me.neznamy.tab.platforms.modded.ModdedTabCommand;
import me.neznamy.tab.shared.TAB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.DEDICATED_SERVER)
@Mod(value = "tab")
public class ForgeTAB {
	public ForgeTAB(final FMLJavaModLoadingContext context) {
		IEventBus EVENT_BUS = context.getModEventBus();
		EVENT_BUS.addListener((RegisterCommandsEvent event) -> new ModdedTabCommand().onRegisterCommands(event.getDispatcher()));
		EVENT_BUS.addListener((ServerStartingEvent event) -> TAB.create(new ForgePlatform(event.getServer(), "Forge")));
		EVENT_BUS.addListener((ServerStoppingEvent event) -> TAB.getInstance().unload());
	}
}
