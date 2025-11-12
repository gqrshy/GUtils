package com.gashi.gutils.music

import com.gashi.gutils.GUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.MovingSoundInstance
import net.minecraft.client.sound.SoundInstance
import net.minecraft.client.sound.TickableSoundInstance
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.math.MathHelper

/**
 * A tickable sound instance for smooth music playback with fade in/out
 *
 * Direct port of Cobblemon 1.6.1/1.7's BattleMusicInstance for Minecraft 1.21.1
 * - Smooth fade in/out using linear interpolation (60 ticks = 3 seconds)
 * - Always loops (like Cobblemon 1.6.1/1.7)
 * - Non-positional (plays at constant volume regardless of player position)
 * - Uses SoundInstance.Attenuation.NONE
 *
 * @author Cobblemon Contributors (original), adapted for GUtils/Minecraft 1.21.1 by Gashi
 */
class BattleMusicInstance private constructor(
    sound: SoundEvent,
    private val targetVolume: Float,
    pitch: Float
) : MovingSoundInstance(
    sound,
    SoundCategory.MUSIC,
    net.minecraft.util.math.random.Random.create()
), TickableSoundInstance {

    companion object {
        /**
         * Create a new BattleMusicInstance that starts at volume 0 and fades in
         */
        fun create(sound: SoundEvent, targetVolume: Float, pitch: Float): BattleMusicInstance {
            return BattleMusicInstance(sound, targetVolume, pitch)
        }
    }

    private val soundManager = MinecraftClient.getInstance().soundManager
    private var fade: Boolean = false
    private var done: Boolean = false
    private var tickCount = 0
    private var fadeCount = 0

    // Fade duration in ticks (60 ticks = 3 seconds at 20 TPS)
    private val fadeTime = 60.0

    // Initial volume (target for fade-in)
    private val initVolume = targetVolume.toDouble()

    init {
        // Match Cobblemon 1.7 exactly - these settings are CRITICAL
        this.volume = 0.0f  // Start at 0 for fade-in
        this.pitch = pitch
        this.x = 0.0
        this.y = 0.0
        this.z = 0.0
        this.relative = true  // CRITICAL: Non-positional sound (plays at constant volume)
        this.repeat = true    // CRITICAL: Loop the music
        this.repeatDelay = 0
        // Note: attenuationType is protected in MovingSoundInstance, set via setAttenuationType if needed

        GUtils.LOGGER.info("Created BattleMusicInstance: $id")
        GUtils.LOGGER.info("  Volume: 0.0 -> $initVolume | Pitch: $pitch")
        GUtils.LOGGER.info("  Relative: $relative | Looping: $repeat")
    }

    // TickableSoundInstance interface
    override fun isDone(): Boolean = done

    override fun tick() {
        tickCount++

        if (fade) {
            // Fade out (matching Cobblemon 1.7 exactly)
            fadeCount++
            this.volume = MathHelper.lerp(fadeCount.toFloat() / fadeTime.toFloat(), initVolume.toFloat(), 0.0f)
            if (this.volume <= 0.0f) {
                this.volume = 0.0f
                done = true
                GUtils.LOGGER.info("Music fade out complete: $id")
            }
        } else {
            // Fade in (matching Cobblemon 1.7 exactly)
            if (this.volume < initVolume.toFloat()) {
                this.volume = MathHelper.lerp(tickCount.toFloat() / fadeTime.toFloat(), 0.0f, initVolume.toFloat())
                if (this.volume >= initVolume.toFloat()) {
                    this.volume = initVolume.toFloat()
                    GUtils.LOGGER.info("Music fade in complete: $id")
                }
            }
        }
    }

    /**
     * Start fading out this music instance
     * Stops looping and gradually reduces volume to 0
     * (Matching Cobblemon 1.7 exactly)
     */
    fun setFade() {
        if (!fade) {
            GUtils.LOGGER.info("Starting fade out: $id")
            fade = true
            this.repeat = false  // Stop looping when fading out
            fadeCount = 0
        }
    }

    /**
     * Check if this instance is currently fading out
     */
    fun isFading(): Boolean = fade
}
