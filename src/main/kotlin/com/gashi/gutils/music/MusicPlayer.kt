package com.gashi.gutils.music

import com.gashi.gutils.GUtils
import com.gashi.gutils.sound.CobbleRankedSounds
import net.minecraft.client.MinecraftClient
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import net.minecraft.registry.Registries

/**
 * Client-side music player with smooth fade in/out
 *
 * Based on Cobblemon 1.7's BattleMusicController and BattleMusicInstance
 * - Smooth fade in when starting playback (3 seconds)
 * - Smooth fade out when stopping (3 seconds)
 * - Non-positional audio (constant volume)
 * - Automatic looping support
 * - Pauses ambient sounds, music, and records while playing (Cobblemon 1.7 feature)
 */
object MusicPlayer {

    private val activeTracks = mutableMapOf<Identifier, BattleMusicInstance>()

    /**
     * Sound categories that are paused while battle music is playing
     * This matches Cobblemon 1.7's filteredCategories
     */
    private val filteredCategories = listOf(
        net.minecraft.sound.SoundCategory.AMBIENT,
        net.minecraft.sound.SoundCategory.MUSIC,
        net.minecraft.sound.SoundCategory.RECORDS
    )

    /**
     * Play a music track with smooth fade-in
     *
     * @param musicId Resource identifier for the music (e.g., "cobbleranked:music.queue.bw_10")
     * @param volume Volume level (0.0 to 1.0+)
     * @param pitch Pitch multiplier (0.5 to 2.0)
     * @param loop Whether to loop the music
     */
    fun play(musicId: Identifier, volume: Float, pitch: Float, loop: Boolean) {
        val client = MinecraftClient.getInstance()
        val soundManager = client.soundManager

        // Stop existing track with same ID (with fade out)
        stop(musicId)

        try {
            GUtils.LOGGER.info("═══════════════════════════════════════════════════════")
            GUtils.LOGGER.info("  Playing music: $musicId")
            GUtils.LOGGER.info("  Volume: $volume | Pitch: $pitch | Loop: $loop")
            GUtils.LOGGER.info("═══════════════════════════════════════════════════════")

            // Get registered sound event from CobbleRankedSounds
            // Sound events MUST be pre-registered, not created dynamically
            val soundEvent = CobbleRankedSounds.getSoundEvent(musicId)

            if (soundEvent == null) {
                GUtils.LOGGER.error("✗ Sound event not registered: $musicId")
                GUtils.LOGGER.error("  Make sure the sound event is added to CobbleRankedSounds.kt")
                return
            }

            GUtils.LOGGER.info("✓ Found registered sound event: $musicId")

            // Create BattleMusicInstance (matching Cobblemon 1.7 exactly)
            // Note: Always loops, matching Cobblemon 1.7's behavior
            val musicInstance = BattleMusicInstance.create(
                sound = soundEvent,
                targetVolume = volume,
                pitch = pitch
            )

            // Play the music
            soundManager.play(musicInstance)
            activeTracks[musicId] = musicInstance

            GUtils.LOGGER.info("✓ Music started with fade-in effect")

        } catch (e: Exception) {
            GUtils.LOGGER.error("✗ Failed to play music: $musicId", e)
            GUtils.LOGGER.error("  Error details: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Stop a specific music track with smooth fade-out
     */
    fun stop(musicId: Identifier) {
        val musicInstance = activeTracks[musicId] ?: return

        try {
            if (!musicInstance.isFading()) {
                GUtils.LOGGER.info("Initiating fade-out for: $musicId")
                musicInstance.setFade()
            }
        } catch (e: Exception) {
            GUtils.LOGGER.error("Failed to stop music: $musicId", e)
        }
    }

    /**
     * Stop all active music tracks with smooth fade-out
     */
    fun stopAll() {
        if (activeTracks.isEmpty()) {
            GUtils.LOGGER.info("No active tracks to stop")
            return
        }

        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")
        GUtils.LOGGER.info("  Stopping all music tracks (${activeTracks.size} total)")
        GUtils.LOGGER.info("═══════════════════════════════════════════════════════")

        // Fade out all tracks
        activeTracks.values.forEach { musicInstance ->
            try {
                if (!musicInstance.isFading()) {
                    musicInstance.setFade()
                }
            } catch (e: Exception) {
                GUtils.LOGGER.error("Failed to fade out music instance", e)
            }
        }

        GUtils.LOGGER.info("✓ All tracks fading out")
    }

    /**
     * Check if a specific track is currently playing
     */
    fun isPlaying(musicId: Identifier): Boolean {
        val musicInstance = activeTracks[musicId] ?: return false
        return !musicInstance.isDone() && !musicInstance.isFading()
    }

    /**
     * Get list of currently playing track IDs
     */
    fun getActiveTracks(): List<Identifier> {
        return activeTracks.keys.toList()
    }

    /**
     * Clean up finished tracks from the active tracks map
     * Called automatically every 10 seconds
     */
    fun cleanupFinishedTracks() {
        val iterator = activeTracks.iterator()
        var removed = 0

        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (entry.value.isDone()) {
                iterator.remove()
                removed++
                GUtils.LOGGER.debug("Cleaned up finished track: ${entry.key}")
            }
        }

        if (removed > 0) {
            GUtils.LOGGER.debug("Cleaned up $removed finished track(s)")
        }
    }
}
