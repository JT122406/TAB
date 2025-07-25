package me.neznamy.tab.api.tablist;

import lombok.NonNull;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An interface allowing to modify TabList display names
 * of players.
 * <p>
 * Instance can be obtained using {@link TabAPI#getTabListFormatManager()}.
 * This requires the feature to be enabled in config, otherwise the method will
 * return {@code null}.
 */
@SuppressWarnings("unused") // API class
public interface TabListFormatManager {

    /**
     * Changes player's prefix to provided value. Supports placeholders,
     * as well as any supported RGB formats. Use {@code null} to reset
     * value back to original.
     *
     * @param   player
     *          player to change prefix of
     * @param   prefix
     *          new prefix value
     * @see     #getCustomPrefix(TabPlayer)
     * @see     #getOriginalRawPrefix(TabPlayer)
     */
    void setPrefix(@NonNull TabPlayer player, @Nullable String prefix);

    /**
     * Changes player's name to provided value. Supports placeholders,
     * as well as any supported RGB formats. Use {@code null} to reset
     * value back to original.
     *
     * @param   player
     *          player to change prefix of
     * @param   customName
     *          new customName value
     * @see     #getCustomName(TabPlayer)
     * @see     #getOriginalRawName(TabPlayer)
     */
    void setName(@NonNull TabPlayer player, @Nullable String customName);

    /**
     * Changes player's suffix to provided value. Supports placeholders,
     * as well as any supported RGB formats. Use {@code null} to reset
     * value back to original.
     *
     * @param   player
     *          player to change prefix of
     * @param   suffix
     *          new suffix value
     * @see     #getCustomSuffix(TabPlayer)
     * @see     #getOriginalRawSuffix(TabPlayer)
     */
    void setSuffix(@NonNull TabPlayer player, @Nullable String suffix);

    /**
     * Returns prefix assigned using {@link #setPrefix(TabPlayer, String)} method.
     * If the method was not used, {@code null} is returned.
     *
     * @param   player
     *          player to get custom prefix of
     * @return  player's custom prefix or {@code null} if not defined
     * @see     #setPrefix(TabPlayer, String)
     * @see     #getOriginalRawPrefix(TabPlayer)
     */
    @Nullable String getCustomPrefix(@NonNull TabPlayer player);

    /**
     * Returns custom name assigned using {@link #setName(TabPlayer, String)} method.
     * If the method was not used, {@code null} is returned.
     *
     * @param   player
     *          player to get custom name of
     * @return  player's custom name or {@code null} if not defined
     * @see     #setName(TabPlayer, String)
     * @see     #getOriginalName(TabPlayer)
     */
    @Nullable String getCustomName(@NonNull TabPlayer player);

    /**
     * Returns suffix assigned using {@link #setSuffix(TabPlayer, String)} method.
     * If the method was not used, {@code null} is returned.
     *
     * @param   player
     *          player to get custom suffix of
     * @return  player's custom suffix or {@code null} if not defined
     * @see     #setSuffix(TabPlayer, String)
     * @see     #getOriginalSuffix(TabPlayer)
     */
    @Nullable String getCustomSuffix(@NonNull TabPlayer player);

    /**
     * Returns player's original prefix applied using plugin's internal logic.
     * This value is not affected by API calls in any way.
     *
     * @param   player
     *          player to get original prefix of
     * @return  player's original prefix
     * @see     #setPrefix(TabPlayer, String)
     * @see     #getCustomPrefix(TabPlayer)
     * @deprecated  Use {@link #getOriginalRawPrefix(TabPlayer)} instead.
     */
    @NotNull
    @Deprecated
    String getOriginalPrefix(@NonNull TabPlayer player);

    /**
     * Returns player's original name applied using plugin's internal logic.
     * This value is not affected by API calls in any way.
     *
     * @param   player
     *          player to get original name of
     * @return  player's original name
     * @see     #setName(TabPlayer, String)
     * @see     #getCustomName(TabPlayer)
     * @deprecated  Use {@link #getOriginalRawName(TabPlayer)} instead.
     */
    @NotNull
    @Deprecated
    String getOriginalName(@NonNull TabPlayer player);

    /**
     * Returns player's original suffix applied using plugin's internal logic.
     * This value is not affected by API calls in any way.
     *
     * @param   player
     *          player to get original suffix of
     * @return  player's original suffix
     * @see     #setSuffix(TabPlayer, String)
     * @see     #getCustomSuffix(TabPlayer)
     * @deprecated  Use {@link #getOriginalRawSuffix(TabPlayer)} instead.
     */
    @NotNull
    @Deprecated
    String getOriginalSuffix(@NonNull TabPlayer player);

    /**
     * Returns original prefix assigned by the plugin using internal logic.
     * @param   player
     *          Player to get original prefix of
     * @return  Original prefix assigned by the plugin's internal logic.
     * @see     #getCustomPrefix(TabPlayer)
     */
    @NotNull
    String getOriginalRawPrefix(@NonNull TabPlayer player);

    /**
     * Returns original name assigned by the plugin using internal logic.
     * @param   player
     *          Player to get original name of
     * @return  Original name assigned by the plugin's internal logic.
     * @see     #getCustomName(TabPlayer)
     */
    @NotNull
    String getOriginalRawName(@NonNull TabPlayer player);

    /**
     * Returns original suffix assigned by the plugin using internal logic.
     * @param   player
     *          Player to get original suffix of
     * @return  Original suffix assigned by the plugin's internal logic.
     * @see     #getCustomSuffix(TabPlayer)
     */
    @NotNull
    String getOriginalRawSuffix(@NonNull TabPlayer player);

    /**
     * Returns original prefix assigned by the plugin using internal logic with all placeholders replaced.
     * @param   player
     *          Player to get replaced original prefix of
     * @return  Original prefix assigned by the plugin's internal logic.
     * @see     #getCustomPrefix(TabPlayer)
     */
    @NotNull
    String getOriginalReplacedPrefix(@NonNull TabPlayer player);

    /**
     * Returns original name assigned by the plugin using internal logic with all placeholders replaced.
     * @param   player
     *          Player to get replaced original name of
     * @return  Original name assigned by the plugin's internal logic.
     * @see     #getCustomName(TabPlayer)
     */
    @NotNull
    String getOriginalReplacedName(@NonNull TabPlayer player);

    /**
     * Returns original suffix assigned by the plugin using internal logic with all placeholders replaced.
     * @param   player
     *          Player to get replaced original suffix of
     * @return  Original suffix assigned by the plugin's internal logic.
     * @see     #getCustomSuffix(TabPlayer)
     */
    @NotNull
    String getOriginalReplacedSuffix(@NonNull TabPlayer player);
}
