package com.gashi.gutils.network

import com.gashi.gutils.GUtils
import com.gashi.gutils.music.MusicPlayer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

/**
 * Handles registration and processing of custom network packets
 */
object NetworkHandler {

    /**
     * Register packet receivers for client-side
     */
    fun registerReceivers() {
        // Register MusicPacket receiver
        ClientPlayNetworking.registerGlobalReceiver(
            MusicPacket.ID
        ) { packet, context ->
            // Execute on client thread (main thread)
            context.client().execute {
                handleMusicPacket(packet)
            }
        }

        GUtils.LOGGER.info("Registered MusicPacket receiver (ID: ${MusicPacket.ID.id()})")
    }

    /**
     * Handle incoming MusicPacket
     */
    private fun handleMusicPacket(packet: MusicPacket) {
        try {
            when (packet.action) {
                MusicPacket.Action.PLAY -> {
                    if (packet.musicId == null) {
                        GUtils.LOGGER.warn("Received PLAY packet with null musicId")
                        return
                    }
                    GUtils.LOGGER.info(
                        "Received PLAY packet: ${packet.musicId} " +
                        "(volume: ${packet.volume}, pitch: ${packet.pitch}, loop: ${packet.loop})"
                    )
                    MusicPlayer.play(packet.musicId, packet.volume, packet.pitch, packet.loop)
                }

                MusicPacket.Action.STOP -> {
                    if (packet.musicId == null) {
                        GUtils.LOGGER.warn("Received STOP packet with null musicId")
                        return
                    }
                    GUtils.LOGGER.info("Received STOP packet: ${packet.musicId}")
                    MusicPlayer.stop(packet.musicId)
                }

                MusicPacket.Action.STOP_ALL -> {
                    GUtils.LOGGER.info("Received STOP_ALL packet")
                    MusicPlayer.stopAll()
                }
            }
        } catch (e: Exception) {
            GUtils.LOGGER.error("Error handling MusicPacket", e)
        }
    }
}
