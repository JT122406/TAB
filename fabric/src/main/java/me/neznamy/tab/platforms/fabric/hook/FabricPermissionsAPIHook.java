package me.neznamy.tab.platforms.fabric.hook;

import me.lucko.fabric.api.permissions.v0.Permissions;
import me.neznamy.tab.platforms.modded.permissions.PermissionsAPIHook;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

/**
 * Class hooking into PermissionsAPI mod for permission nodes instead
 * of only using the integrated OP level.
 */
public class FabricPermissionsAPIHook implements PermissionsAPIHook {

    /**
     * Checks for permission and returns the result.
     *
     * @param   source
     *          Source to check permission of
     * @param   permission
     *          Permission node to check
     * @return  {@code true} if has permission, {@code false} if not
     */
    @Override
    public boolean hasPermission(@NotNull CommandSourceStack source, @NotNull String permission) {
        if (PermissionsAPIHook.super.hasPermission(source, permission)) return true;
        return Permissions.check(source, permission);
    }
}
