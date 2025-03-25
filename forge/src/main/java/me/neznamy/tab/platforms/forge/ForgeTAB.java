package me.neznamy.tab.platforms.forge;

import me.neznamy.tab.shared.TAB;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.DEDICATED_SERVER)
@Mod(value = "tab")
public class ForgeTAB {

	public ForgeTAB(final FMLJavaModLoadingContext context) {
		IEventBus EVENT_BUS = context.getModEventBus();
		EVENT_BUS.addListener((RegisterCommandsEvent event) -> new NeoForgeTabCommand().onRegisterCommands(event.getDispatcher()));
		EVENT_BUS.addListener((ServerStartingEvent event) -> TAB.create(new NeoForgePlatform(event.getServer())));
		EVENT_BUS.addListener((ServerStoppingEvent event) -> TAB.getInstance().unload());
	}

	@NotNull
	public static String getLevelName(@NotNull Level level) {
		String path = level.dimension().location().getPath();
		return ((ServerLevelData)level.getLevelData()).getLevelName() + switch (path) {
			case "overworld" -> ""; // No suffix for overworld
			case "the_nether" -> "_nether";
			default -> "_" + path; // End + default behavior for other dimensions created by mods
		};
	}
}
