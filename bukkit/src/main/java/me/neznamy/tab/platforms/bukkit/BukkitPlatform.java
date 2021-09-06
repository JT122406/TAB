package me.neznamy.tab.platforms.bukkit;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.Essentials;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import me.clip.placeholderapi.PlaceholderAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.chat.EnumChatFormat;
import me.neznamy.tab.api.protocol.PacketBuilder;
import me.neznamy.tab.platforms.bukkit.event.TabPlayerLoadEvent;
import me.neznamy.tab.platforms.bukkit.event.TabLoadEvent;
import me.neznamy.tab.platforms.bukkit.features.PerWorldPlayerlist;
import me.neznamy.tab.platforms.bukkit.features.PetFix;
import me.neznamy.tab.platforms.bukkit.features.TabExpansion;
import me.neznamy.tab.platforms.bukkit.features.WitherBossBar;
import me.neznamy.tab.platforms.bukkit.features.unlimitedtags.NameTagX;
import me.neznamy.tab.platforms.bukkit.nms.NMSStorage;
import me.neznamy.tab.platforms.bukkit.permission.Vault;
import me.neznamy.tab.shared.Platform;
import me.neznamy.tab.shared.TAB;
import me.neznamy.tab.shared.features.NameTag;
import me.neznamy.tab.shared.features.PlaceholderManagerImpl;
import me.neznamy.tab.shared.features.bossbar.BossBarManagerImpl;
import me.neznamy.tab.shared.permission.LuckPerms;
import me.neznamy.tab.shared.permission.None;
import me.neznamy.tab.shared.permission.PermissionPlugin;
import me.neznamy.tab.shared.permission.UltraPermissions;
import me.neznamy.tab.shared.placeholders.PlayerPlaceholder;
import me.neznamy.tab.shared.placeholders.UniversalPlaceholderRegistry;
import net.milkbowl.vault.permission.Permission;

/**
 * Bukkit implementation of Platform
 */
public class BukkitPlatform implements Platform {

	//plugin instance
	private Main plugin;

	//nms storage
	private NMSStorage nms;

	private BukkitPacketBuilder packetBuilder;

	//booleans to check plugin presence
	private Plugin placeholderAPI;
	private boolean viaversion;
	private boolean idisguise;
	private boolean libsdisguises;
	private Plugin essentials;

	/**
	 * Constructs new instance with given parameters
	 * @param plugin - plugin instance
	 * @param nms - nms storage
	 */
	public BukkitPlatform(Main plugin, NMSStorage nms) {
		this.plugin = plugin;
		this.nms = nms;
		packetBuilder = new BukkitPacketBuilder(nms);
	}

	@Override
	public PermissionPlugin detectPermissionPlugin() {
		if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
			return new LuckPerms(Bukkit.getPluginManager().getPlugin("LuckPerms").getDescription().getVersion());
		} else if (Bukkit.getPluginManager().isPluginEnabled("UltraPermissions")) {
			return new UltraPermissions(Bukkit.getPluginManager().getPlugin("UltraPermissions").getDescription().getVersion());
		} else if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
			return new Vault(Bukkit.getServicesManager().getRegistration(Permission.class).getProvider(), Bukkit.getPluginManager().getPlugin("Vault").getDescription().getVersion());
		} else {
			return new None();
		}
	}

	@Override
	public void loadFeatures() {
		placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
		viaversion = Bukkit.getPluginManager().isPluginEnabled("ViaVersion");
		idisguise = Bukkit.getPluginManager().isPluginEnabled("iDisguise");
		libsdisguises = Bukkit.getPluginManager().isPluginEnabled("LibsDisguises");
		essentials = Bukkit.getPluginManager().getPlugin("Essentials");
		TAB tab = TAB.getInstance();
		if (tab.getConfiguration().isPipelineInjection()) tab.getFeatureManager().registerFeature("injection", new BukkitPipelineInjector(nms));
		loadNametagFeature(tab);
		tab.loadUniversalFeatures();
		new BukkitPlaceholderRegistry().registerPlaceholders(tab.getPlaceholderManager());
		new UniversalPlaceholderRegistry().registerPlaceholders(tab.getPlaceholderManager());
		if (tab.getConfiguration().getConfig().getBoolean("bossbar.enabled", false)) {
			if (nms.getMinorVersion() < 9) {
				tab.getFeatureManager().registerFeature("bossbar", new WitherBossBar(plugin));
			} else {
				tab.getFeatureManager().registerFeature("bossbar", new BossBarManagerImpl());
			}
		}
		if (nms.getMinorVersion() >= 9 && tab.getConfiguration().getConfig().getBoolean("fix-pet-names.enabled", false)) tab.getFeatureManager().registerFeature("petfix", new PetFix(nms));
		if (tab.getConfiguration().getConfig().getBoolean("per-world-playerlist.enabled", false)) tab.getFeatureManager().registerFeature("pwp", new PerWorldPlayerlist(plugin, tab));
		if (placeholderAPI != null) {
			new TabExpansion(plugin);
		}
		for (Player p : getOnlinePlayers()) {
			tab.addPlayer(new BukkitTabPlayer(p, plugin.getProtocolVersion(p)));
		}
	}

	/**
	 * Loads nametag feature from config
	 * @param tab - tab instance
	 */
	private void loadNametagFeature(TAB tab) {
		if (tab.getConfiguration().getConfig().getBoolean("scoreboard-teams.enabled", true)) {
			if (tab.getConfiguration().getConfig().getBoolean("scoreboard-teams.unlimited-nametag-mode.enabled", false) && nms.getMinorVersion() >= 8) {
				tab.getFeatureManager().registerFeature("nametagx", new NameTagX(plugin, nms));
			} else {
				tab.getFeatureManager().registerFeature("nametag16", new NameTag());
			}
		}
	}

	/**
	 * Returns list of online players from Bukkit API
	 * @return list of online players from Bukkit API
	 */
	@SuppressWarnings("unchecked")
	private Player[] getOnlinePlayers() {
		try {
			Object players = Bukkit.class.getMethod("getOnlinePlayers").invoke(null);
			if (players instanceof Player[]) {
				//1.7.x
				return (Player[]) players;
			} else {
				//1.8+
				return ((Collection<Player>)players).toArray(new Player[0]); 
			}
		} catch (Exception e) {
			TAB.getInstance().getErrorManager().printError("Failed to get online players", e);
			return new Player[0];
		}
	}

	@Override
	public void sendConsoleMessage(String message, boolean translateColors) {
		Bukkit.getConsoleSender().sendMessage(translateColors ? EnumChatFormat.color(message) : message);
	}

	@Override
	public void registerUnknownPlaceholder(String identifier) {
		PlaceholderManagerImpl pl = TAB.getInstance().getPlaceholderManager();
		if (identifier.startsWith("%rel_")) {
			//relational placeholder
			registerRelationalPlaceholder(identifier, pl.getRelationalRefresh(identifier));
		} else {
			//normal placeholder
			if (identifier.startsWith("%sync:")) {
				int refresh;
				if (pl.getServerPlaceholderRefreshIntervals().containsKey(identifier)) {
					refresh = pl.getServerPlaceholderRefreshIntervals().get(identifier);
				} else if (pl.getPlayerPlaceholderRefreshIntervals().containsKey(identifier)) {
					refresh = pl.getPlayerPlaceholderRefreshIntervals().get(identifier);
				} else {
					refresh = pl.getDefaultRefresh();
				}
				pl.registerPlaceholder(new PlayerPlaceholder(identifier, refresh) {
					
					@Override
					public Object get(TabPlayer p) {
						Bukkit.getScheduler().runTask(plugin, () -> {

							long time = System.nanoTime();
							String syncedPlaceholder = identifier.substring(6, identifier.length()-1);
							String value = ((BukkitPlatform) TAB.getInstance().getPlatform()).setPlaceholders((Player) p.getPlayer(), "%" + syncedPlaceholder + "%");
							getLastValues().put(p.getName(), value);
							if (!getForceUpdate().contains(p.getName())) getForceUpdate().add(p.getName());
							TAB.getInstance().getCPUManager().addPlaceholderTime(getIdentifier(), System.nanoTime()-time);
						});
						return getLastValues().get(p.getName());
					}
				});
				return;
			}
			if (pl.getServerPlaceholderRefreshIntervals().containsKey(identifier)) {
				TAB.getInstance().getPlaceholderManager().registerServerPlaceholder(identifier, pl.getServerPlaceholderRefreshIntervals().get(identifier), () -> setPlaceholders(null, identifier));
			} else {
				int refresh;
				if (pl.getPlayerPlaceholderRefreshIntervals().containsKey(identifier)) {
					refresh = pl.getPlayerPlaceholderRefreshIntervals().get(identifier);
				} else {
					refresh = pl.getDefaultRefresh();
				}
				TAB.getInstance().getPlaceholderManager().registerPlayerPlaceholder(identifier, refresh, p -> setPlaceholders((Player) p.getPlayer(), identifier));
			}
		}
	}

	/**
	 * Registers relational placeholder
	 * @param identifier - placeholder identifier
	 * @param refresh - refresh interval in milliseconds
	 */
	private void registerRelationalPlaceholder(String identifier, int refresh) {
		TAB.getInstance().getPlaceholderManager().registerRelationalPlaceholder(identifier, refresh, (viewer, target) -> {
			if (placeholderAPI == null) return identifier;
			try {
				return PlaceholderAPI.setRelationalPlaceholders((Player) viewer.getPlayer(), (Player) target.getPlayer(), identifier);
			} catch (Exception | NoClassDefFoundError t) {
				if (TAB.getInstance().isDebugMode()) {
					TAB.getInstance().getErrorManager().printError("PlaceholderAPI v" + placeholderAPI.getDescription().getVersion() + 
							" generated an error when setting relational placeholder " + identifier, t);
				}
				return identifier;
			}
		});
	}

	/**
	 * Runs PlaceholderAPI call and returns the output. Returns identifier if call throws an exception.
	 * @param player - player to set placeholder for
	 * @param placeholder - placeholder
	 * @return result from PlaceholderAPI
	 */
	public String setPlaceholders(Player player, String placeholder) {
		if (placeholderAPI == null) return placeholder;
		try {
			return PlaceholderAPI.setPlaceholders(player, placeholder);
		} catch (Exception | NoClassDefFoundError t) {
			if (TAB.getInstance().isDebugMode()) {
				TAB.getInstance().getErrorManager().printError("PlaceholderAPI v" + placeholderAPI.getDescription().getVersion() + 
						" generated an error when setting placeholder " + placeholder + (player == null ? "" : " for player " + player.getName()), t);
			}
			return "ERROR";
		}
	}

	@Override
	public String getServerVersion() {
		return Bukkit.getBukkitVersion().split("-")[0] + " (" + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + ")";
	}

	@Override
	public String getSeparatorType() {
		return "world";
	}

	@Override
	public File getDataFolder() {
		return plugin.getDataFolder();
	}

	@Override
	public void callLoadEvent() {
		Bukkit.getPluginManager().callEvent(new TabLoadEvent());
	}

	@Override
	public void callLoadEvent(TabPlayer player) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> Bukkit.getPluginManager().callEvent(new TabPlayerLoadEvent(player)));
	}

	@Override
	public int getMaxPlayers() {
		return Bukkit.getMaxPlayers();
	}

	@Override
	public String getConfigName() {
		return "bukkitconfig.yml";
	}

	public boolean isViaversionEnabled() {
		return viaversion;
	}

	public boolean isLibsdisguisesEnabled() {
		return libsdisguises;
	}

	public boolean isIdisguiseEnabled() {
		return idisguise;
	}

	public Essentials getEssentials() {
		return (Essentials) essentials;
	}

	@Override
	public PacketBuilder getPacketBuilder() {
		return packetBuilder;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getSkin(List<String> properties) {
		PropertyMap map = new PropertyMap();
		map.put("textures", new Property("textures", properties.get(0), properties.get(1)));
		return map;
	}
}