package com.gashi.gutils.music

import com.gashi.gutils.GUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.PositionedSoundInstance
import net.minecraft.client.sound.SoundInstance
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import net.minecraft.registry.Registries

/**
 * Client-side music player
 *
 * Manages playing, stopping, and tracking custom music tracks
 */
object MusicPlayer {

    private val activeTracks = mutableMapOf<Identifier, SoundInstance>()

    /**
     * Play a music track
     *
     * @param musicId Resource identifier for the music (e.g., "cobbleranked:team_selection_music")
     * @param volume Volume level (0.0 to 1.0+)
     * @param pitch Pitch multiplier (0.5 to 2.0)
     * @param loop Whether to loop the music
     */
    fun play(musicId: Identifier, volume: Float, pitch: Float, loop: Boolean) {
        val client = MinecraftClient.getInstance()
        val soundManager = client.soundManager

        // Stop existing track with same ID
        stop(musicId)

        try {
            // Get sound event from registry
            val soundEventOptional = Registries.SOUND_EVENT.getOrEmpty(musicId)

            if (soundEventOptional.isEmpty) {
                GUtils.LOGGER.warn("Sound event not found in registry: $musicId")
                GUtils.LOGGER.warn("Make sure the sound is registered in your resource pack or mod")
                return
            }

            val soundEvent = soundEventOptional.get()

            // Create sound instance
            val soundInstance = PositionedSoundInstance(
                soundEvent,
                SoundCategory.MUSIC,
                volume,
                pitch,
                if (loop) SoundInstance.createRandom() else null,  // Random seed for looping
                loop,  // repeat
                0,  // repeatDelay
                SoundInstance.AttenuationType.NONE,  // No distance attenuation for music
                0.0,  // x
                0.0,  // y
                0.0,  // z
                true  // relative
            )

            // Play the sound
            soundManager.play(soundInstance)
            activeTracks[musicId] = soundInstance

            GUtils.LOGGER.info("Playing music: $musicId (volume: $volume, pitch: $pitch, loop: $loop)")

        } catch (e: Exception) {
            GUtils.LOGGER.error("Failed to play music: $musicId", e)
        }
    }

    /**
     * Stop a specific music track
     */
    fun stop(musicId: Identifier) {
        val soundInstance = activeTracks.remove(musicId) ?: return

        try {
            val client = MinecraftClient.getInstance()
            client.soundManager.stop(soundInstance)
            GUtils.LOGGER.info("Stopped music: $musicId")
        } catch (e: Exception) {
            GUtils.LOGGER.error("Failed to stop music: $musicId", e)
        }
    }

    /**
     * Stop all active music tracks
     */
    fun stopAll() {
        if (activeTracks.isEmpty()) {
            GUtils.LOGGER.info("No active tracks to stop")
            return
        }

        GUtils.LOGGER.info("Stopping all active tracks (${activeTracks.size} total)")

        val client = MinecraftClient.getInstance()
        val soundManager = client.soundManager

        // Stop all tracks
        activeTracks.values.forEach { soundInstance ->
            try {
                soundManager.stop(soundInstance)
            } catch (e: Exception) {
                GUtils.LOGGER.error("Failed to stop sound instance", e)
            }
        }

        activeTracks.clear()
        GUtils.LOGGER.info("All music tracks stopped")
    }

    /**
     * Check if a specific track is currently playing
     */
    fun isPlaying(musicId: Identifier): Boolean {
        val soundInstance = activeTracks[musicId] ?: return false
        val client = MinecraftClient.getInstance()
        return client.soundManager.isPlaying(soundInstance)
    }

    /**
     * Get list of currently playing track IDs
     */
    fun getActiveTracks(): List<Identifier> {
        return activeTracks.keys.toList()
    }

    /**
     * Clear finished tracks from the active tracks map
     */
    fun cleanupFinishedTracks() {
        val client = MinecraftClient.getInstance()
        val soundManager = client.soundManager

        val iterator = activeTracks.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (!soundManager.isPlaying(entry.value)) {
                iterator.remove()
                GUtils.LOGGER.debug("Removed finished track: ${entry.key}")
            }
        }
    }
}
