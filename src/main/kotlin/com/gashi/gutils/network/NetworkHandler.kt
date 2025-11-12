package com.gashi.gutils.network

import com.gashi.gutils.GUtils
import com.gashi.gutils.debug.ResourcePackDiagnostics
import com.gashi.gutils.music.MusicPlayer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Handles registration and processing of custom network packets
 */
object NetworkHandler {

    private val diagnosticsExecutor = Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r, "GUtils-Diagnostics").apply { isDaemon = true }
    }

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
     * Register client event handlers
     */
    fun registerEventHandlers() {
        // Send capability packet when joining a server
        ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
            try {
                val payload = ClientCapabilityPayload(
                    hasGUtilsMod = true,
                    modVersion = GUtils.VERSION
                )

                // Send capability packet to server
                sender.sendPacket(payload)

                GUtils.LOGGER.info("Sent client capability packet to server (version: ${GUtils.VERSION})")

                // Schedule resource pack diagnostics after a delay
                // This allows time for the server resource pack to be downloaded and applied
                diagnosticsExecutor.schedule({
                    try {
                        client.execute {
                            ResourcePackDiagnostics.runFullDiagnostics()
                        }
                    } catch (e: Exception) {
                        GUtils.LOGGER.error("Failed to run resource pack diagnostics", e)
                    }
                }, 5, TimeUnit.SECONDS)

            } catch (e: Exception) {
                GUtils.LOGGER.error("Failed to send client capability packet", e)
            }
        }

        GUtils.LOGGER.info("Registered client event handlers")
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
