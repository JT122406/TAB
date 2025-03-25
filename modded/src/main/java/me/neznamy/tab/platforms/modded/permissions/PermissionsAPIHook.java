package me.neznamy.tab.platforms.modded.permissions;

import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

public interface PermissionsAPIHook {
    default boolean hasPermission(@NotNull CommandSourceStack sourceStack, @NotNull String permission) {
        return sourceStack.hasPermission(4);
    }
}
