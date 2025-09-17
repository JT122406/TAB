package me.neznamy.tab.platforms.bukkit.v1_20_R3;

import me.neznamy.tab.shared.chat.TabStyle;
import me.neznamy.tab.shared.chat.component.object.TabAtlasSprite;
import me.neznamy.tab.shared.chat.component.object.TabObjectComponent;
import me.neznamy.tab.platforms.bukkit.provider.ComponentConverter;
import me.neznamy.tab.shared.chat.component.object.TabPlayerSprite;
import net.minecraft.network.chat.*;
import net.minecraft.resources.MinecraftKey;
import org.jetbrains.annotations.NotNull;

/**
 * Component converter using direct NMS code.
 */
public class NMSComponentConverter extends ComponentConverter {

    @Override
    @NotNull
    public Object newTextComponent(@NotNull String text) {
        return IChatBaseComponent.b(text);
    }

    @Override
    @NotNull
    public Object newTranslatableComponent(@NotNull String key) {
        return IChatBaseComponent.c(key);
    }

    @Override
    @NotNull
    public Object newKeybindComponent(@NotNull String keybind) {
        return IChatBaseComponent.d(keybind);
    }

    @Override
    @NotNull
    public Object newObjectComponent(@NotNull TabAtlasSprite sprite) {
        return IChatBaseComponent.b(TabObjectComponent.ERROR_MESSAGE);
    }

    @Override
    @NotNull
    public Object newObjectComponent(@NotNull TabPlayerSprite sprite) {
        return IChatBaseComponent.b(TabObjectComponent.ERROR_MESSAGE);
    }

    @Override
    public void applyStyle(@NotNull Object nmsComponent, @NotNull TabStyle modifier) {
        ((IChatMutableComponent)nmsComponent).c(ChatModifier.a
                .a(modifier.getColor() == null ? null : ChatHexColor.a(modifier.getColor().getRgb()))
                .a(modifier.getBold())
                .b(modifier.getItalic())
                .c(modifier.getUnderlined())
                .d(modifier.getStrikethrough())
                .e(modifier.getObfuscated())
                .a(modifier.getFont() == null ? null : MinecraftKey.a(modifier.getFont()))
        );
    }

    @Override
    public void addSibling(@NotNull Object parent, @NotNull Object child) {
        ((IChatMutableComponent)parent).b((IChatBaseComponent) child);
    }
}
