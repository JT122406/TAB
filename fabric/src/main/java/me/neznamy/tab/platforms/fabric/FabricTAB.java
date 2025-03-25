package me.neznamy.tab.platforms.fabric;

import me.neznamy.tab.shared.TAB;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

/**
 * Main class for Fabric.
 */
public class FabricTAB implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext, commandSelection) -> new FabricTabCommand().onRegisterCommands(dispatcher));
        ServerLifecycleEvents.SERVER_STARTING.register(server -> TAB.create(new FabricPlatform(server, "Fabric")));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> TAB.getInstance().unload());
    }
}
