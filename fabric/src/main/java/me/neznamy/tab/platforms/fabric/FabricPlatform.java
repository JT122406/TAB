package me.neznamy.tab.platforms.fabric;

import com.google.common.collect.ImmutableMultimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.neznamy.tab.platforms.fabric.hook.FabricTabExpansion;
import me.neznamy.tab.shared.TAB;
import me.neznamy.tab.shared.TabConstants;
import me.neznamy.tab.shared.backend.BackendPlatform;
import me.neznamy.tab.shared.chat.ChatModifier;
import me.neznamy.tab.shared.chat.component.KeybindComponent;
import me.neznamy.tab.shared.chat.component.TabComponent;
import me.neznamy.tab.shared.chat.component.TextComponent;
import me.neznamy.tab.shared.chat.component.TranslatableComponent;
import me.neznamy.tab.shared.chat.component.object.AtlasSprite;
import me.neznamy.tab.shared.chat.component.object.ObjectComponent;
import me.neznamy.tab.shared.chat.component.object.PlayerSprite;
import me.neznamy.tab.shared.features.PerWorldPlayerListConfiguration;
import me.neznamy.tab.shared.features.PlaceholderManagerImpl;
import me.neznamy.tab.shared.features.injection.PipelineInjector;
import me.neznamy.tab.shared.features.types.TabFeature;
import me.neznamy.tab.shared.placeholders.expansion.EmptyTabExpansion;
import me.neznamy.tab.shared.placeholders.expansion.TabExpansion;
import me.neznamy.tab.shared.platform.BossBar;
import me.neznamy.tab.shared.platform.Scoreboard;
import me.neznamy.tab.shared.platform.TabList;
import me.neznamy.tab.shared.platform.TabPlayer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.component.ResolvableProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * Platform implementation for Fabric
 *
 * @param server Minecraft server reference
 */
public record FabricPlatform(MinecraftServer server) implements BackendPlatform {

    /** Empty UUID */
    private static final UUID NIL_UUID = new UUID(0, 0);

    @Override
    public void registerUnknownPlaceholder(@NotNull String identifier) {
        if (!FabricLoader.getInstance().isModLoaded("placeholder-api")) {
            registerDummyPlaceholder(identifier);
            return;
        }

        PlaceholderManagerImpl manager = TAB.getInstance().getPlaceholderManager();
        manager.registerPlayerPlaceholder(identifier,
                p -> Placeholders.parseText(
                        Component.literal(identifier),
                        PlaceholderContext.of((ServerPlayer) p.getPlayer())
                ).getString()
        );
    }

    @Override
    public void loadPlayers() {
        for (ServerPlayer player : getOnlinePlayers()) {
            TAB.getInstance().addPlayer(new FabricTabPlayer(this, player));
        }
    }

    private Collection<ServerPlayer> getOnlinePlayers() {
        // It's nullable on startup
        return server.getPlayerList() == null ? Collections.emptyList() : server.getPlayerList().getPlayers();
    }

    @Override
    @NotNull
    public PipelineInjector createPipelineInjector() {
        return new FabricPipelineInjector();
    }

    @Override
    @NotNull
    public TabExpansion createTabExpansion() {
        if (FabricLoader.getInstance().isModLoaded("placeholder-api"))
            return new FabricTabExpansion();
        return new EmptyTabExpansion();
    }

    @Override
    @Nullable
    public TabFeature getPerWorldPlayerList(@NotNull PerWorldPlayerListConfiguration configuration) {
        return null;
    }

    @Override
    public void logInfo(@NotNull TabComponent message) {
        LogUtils.getLogger().info("[TAB] {}", ((Component) message.convert()).getString());
    }

    @Override
    public void logWarn(@NotNull TabComponent message) {
        LogUtils.getLogger().warn("[TAB] {}", ((Component) message.convert()).getString());
    }

    @Override
    @NotNull
    public String getServerVersionInfo() {
        return "[Fabric] " + SharedConstants.getCurrentVersion().name();
    }

    @Override
    public void registerListener() {
        new FabricEventListener().register();
    }

    @Override
    public void registerCommand() {
        // Event listener must be registered in main class
    }

    @Override
    public void startMetrics() {
        // Not available
    }

    @Override
    @NotNull
    public File getDataFolder() {
        return FabricLoader.getInstance().getConfigDir().resolve(TabConstants.PLUGIN_ID).toFile();
    }

    @Override
    @NotNull
    public Component convertComponent(@NotNull TabComponent component) {
        // Component type
        MutableComponent nmsComponent = switch (component) {
            case TextComponent text -> Component.literal(text.getText());
            case TranslatableComponent translatable -> Component.translatable(translatable.getKey());
            case KeybindComponent keybind -> Component.keybind(keybind.getKeybind());
            case ObjectComponent object -> {
                if (object.getContents() instanceof AtlasSprite sprite) {
                    yield Component.object(new net.minecraft.network.chat.contents.objects.AtlasSprite(
                            ResourceLocation.parse(sprite.getAtlas()),
                            ResourceLocation.parse(sprite.getSprite())
                    ));
                }
                if (object.getContents() instanceof PlayerSprite sprite) {
                    yield Component.object(new net.minecraft.network.chat.contents.objects.PlayerSprite(spriteToProfile(sprite), sprite.isShowHat()));
                }
                throw new IllegalStateException("Unexpected object component type: " + object.getContents().getClass().getName());
            }
            default -> throw new IllegalStateException("Unexpected component type: " + component.getClass().getName());
        };

        // Component style
        ChatModifier modifier = component.getModifier();
        Style style = Style.EMPTY
                .withColor(modifier.getColor() == null ? null : TextColor.fromRgb(modifier.getColor().getRgb()))
                .withBold(modifier.getBold())
                .withItalic(modifier.getItalic())
                .withUnderlined(modifier.getUnderlined())
                .withStrikethrough(modifier.getStrikethrough())
                .withObfuscated(modifier.getObfuscated())
                .withFont(modifier.getFont() == null ? null : new FontDescription.Resource(ResourceLocation.parse(modifier.getFont())));
        if (modifier.getShadowColor() != null) style = style.withShadowColor(modifier.getShadowColor());
        nmsComponent.setStyle(style);

        // Extra
        for (TabComponent extra : component.getExtra()) {
            nmsComponent.getSiblings().add(convertComponent(extra));
        }

        return nmsComponent;
    }

    @NotNull
    @SneakyThrows
    private ResolvableProfile spriteToProfile(@NonNull PlayerSprite sprite) {
        if (sprite.getId() != null) {
            return ResolvableProfile.createUnresolved(sprite.getId());
        } else if (sprite.getName() != null) {
            // Moyank pls
            Constructor<ResolvableProfile.Dynamic> cons = ResolvableProfile.Dynamic.class.getDeclaredConstructor(Either.class);
            cons.setAccessible(true);
            return cons.newInstance(Either.left(sprite.getName()));
        } else if (sprite.getSkin() != null) {
            return ResolvableProfile.createResolved(profileFromSkin(sprite.getSkin()));
        } else {
            throw new IllegalStateException("Player head component does not have id, name or skin set");
        }
    }

    @NotNull
    private GameProfile profileFromSkin(@NonNull TabList.Skin skin) {
        ImmutableMultimap.Builder<String, Property> builder = ImmutableMultimap.builder();
        builder.put(TabList.TEXTURES_PROPERTY, new Property(TabList.TEXTURES_PROPERTY, skin.getValue(), skin.getSignature()));
        return new GameProfile(NIL_UUID, "", new PropertyMap(builder.build()));
    }

    @Override
    @NotNull
    public Scoreboard createScoreboard(@NotNull TabPlayer player) {
        return new FabricScoreboard((FabricTabPlayer) player);
    }

    @Override
    @NotNull
    public BossBar createBossBar(@NotNull TabPlayer player) {
        return new FabricBossBar((FabricTabPlayer) player);
    }

    @Override
    @NotNull
    public TabList createTabList(@NotNull TabPlayer player) {
        return new FabricTabList((FabricTabPlayer) player);
    }

    @Override
    public boolean supportsScoreboards() {
        return true;
    }

    @Override
    public double getTPS() {
        double mspt = getMSPT();
        if (mspt < 50) return 20;
        return Math.round(1000 / mspt);
    }

    @Override
    public double getMSPT() {
        return (float) server.getAverageTickTimeNanos() / 1000000;
    }
}
