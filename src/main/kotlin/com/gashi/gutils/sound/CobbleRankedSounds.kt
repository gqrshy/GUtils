package com.gashi.gutils.sound

import com.gashi.gutils.GUtils
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier

/**
 * Registers custom sound events for CobbleRanked music
 *
 * This class pre-registers all sound events that can be sent from CobbleRanked server
 * to avoid "sound not found" errors when playing music.
 *
 * Sound events MUST be registered during mod initialization, not created dynamically.
 * This is a Minecraft limitation - SoundEvent.of() creates unregistered events that
 * won't play correctly.
 */
object CobbleRankedSounds {

    /**
     * Cache of registered sound events by identifier for fast lookup
     * This allows MusicPlayer to get registered events instead of creating new ones
     *
     * IMPORTANT: This MUST be declared FIRST before any registerSound() calls
     * to avoid NullPointerException during static initialization
     */
    private val soundEventCache = mutableMapOf<String, SoundEvent>()

    // Battle Music - Normal Elo
    val MUSIC_BATTLE_NORMAL_BDSP_TRAINER = registerSound("music.battle.normal.bdsp_trainer")
    val MUSIC_BATTLE_NORMAL_BW_BATTLE_SUBWAY = registerSound("music.battle.normal.bw_battle_subway")
    val MUSIC_BATTLE_NORMAL_DPPT_TRAINER = registerSound("music.battle.normal.dppt_trainer")
    val MUSIC_BATTLE_NORMAL_HGSS_TRAINER_JOHTO = registerSound("music.battle.normal.hgss_trainer_johto")
    val MUSIC_BATTLE_NORMAL_HGSS_TRAINER_KANTO = registerSound("music.battle.normal.hgss_trainer_kanto")
    val MUSIC_BATTLE_NORMAL_ORAS_TRAINER = registerSound("music.battle.normal.oras_trainer")
    val MUSIC_BATTLE_NORMAL_SM_TRAINER = registerSound("music.battle.normal.sm_trainer")
    val MUSIC_BATTLE_NORMAL_SWSH_TRAINER = registerSound("music.battle.normal.swsh_trainer")
    val MUSIC_BATTLE_NORMAL_USUM_TRAINER = registerSound("music.battle.normal.usum_trainer")
    val MUSIC_BATTLE_NORMAL_XY_TRAINER = registerSound("music.battle.normal.xy_trainer")

    // Battle Music - High Elo
    val MUSIC_BATTLE_HIGH_ELO_BATTLE_LOREKEEPER_ZINNIA_HOENN = registerSound("music.battle.high_elo.battle_lorekeeper_zinnia_hoenn")
    val MUSIC_BATTLE_HIGH_ELO_VGC_BATTLE_MUSIC = registerSound("music.battle.high_elo.vgc_battle_music")

    // Queue Music
    val MUSIC_QUEUE_BATTLE_FACTORY_SINNOH = registerSound("music.queue.battle_factory_sinnoh")
    val MUSIC_QUEUE_BW_10 = registerSound("music.queue.bw_10")
    val MUSIC_QUEUE_HALL_OF_FAME_SINNOH = registerSound("music.queue.hall_of_fame_sinnoh")
    val MUSIC_QUEUE_ROUTE113_HOENN2 = registerSound("music.queue.route113_hoenn2")
    val MUSIC_QUEUE_SOUTH_PROVINCE_PALDEA = registerSound("music.queue.south_province_paldea")
    val MUSIC_QUEUE_SYCAMORE = registerSound("music.queue.sycamore")

    // Selection Music
    val MUSIC_SELECTION_TEAM_SELECTION_MUSIC = registerSound("music.selection.team_selection_music")
    val MUSIC_SELECTION_SMVICTORY = registerSound("music.selection.smvictory")
    val MUSIC_SELECTION_WORLDT = registerSound("music.selection.worldt")

    /**
     * Register a sound event to Minecraft's sound registry
     *
     * @param path Sound event path (e.g., "music.battle.normal.bdsp_trainer")
     * @return Registered SoundEvent instance
     */
    private fun registerSound(path: String): SoundEvent {
        val identifier = Identifier.of("cobbleranked", path)
        val soundEvent = SoundEvent.of(identifier)

        Registry.register(Registries.SOUND_EVENT, identifier, soundEvent)
        soundEventCache[identifier.toString()] = soundEvent

        GUtils.LOGGER.info("✓ Registered sound event: $identifier")
        return soundEvent
    }

    /**
     * Get a registered sound event by identifier
     *
     * @param identifier Full identifier (e.g., "cobbleranked:music.battle.normal.bdsp_trainer")
     * @return Registered SoundEvent or null if not found
     */
    fun getSoundEvent(identifier: Identifier): SoundEvent? {
        return soundEventCache[identifier.toString()]
    }

    /**
     * Initialize sound event registration
     * Called during mod initialization
     */
    fun initialize() {
        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")
        GUtils.LOGGER.info("  Registering CobbleRanked sound events...")
        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")

        // Force initialization of all static fields (triggers registerSound calls)
        val totalSounds = soundEventCache.size

        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")
        GUtils.LOGGER.info("  ✓ Registered $totalSounds CobbleRanked sound events")
        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")
    }
}
