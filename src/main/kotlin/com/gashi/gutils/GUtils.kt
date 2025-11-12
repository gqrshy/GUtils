package com.gashi.gutils

import com.gashi.gutils.music.MusicPlayer
import com.gashi.gutils.network.NetworkHandler
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * GUtils - Gashi's Utilities
 *
 * Client-side utility mod providing various features for Cobblemon servers
 *
 * Current Features:
 * - Custom Music Player: Receive and play custom BGM from compatible servers
 *
 * Compatible with:
 * - Minecraft 1.21.1
 * - Cobblemon 1.6.1+
 * - Fabric Loader 0.16.5+
 *
 * This mod provides a custom network packet system to play music on clients
 * running Cobblemon 1.6.1, which doesn't support Cobblemon 1.7's BattleMusicPacket.
 */
object GUtils : ClientModInitializer {

    const val MOD_ID = "gutils"
    const val MOD_NAME = "GUtils"
    const val VERSION = "1.0.0"

    val LOGGER: Logger = LoggerFactory.getLogger(MOD_NAME)

    private var tickCounter = 0
    private const val CLEANUP_INTERVAL_TICKS = 200  // Clean up every 10 seconds (20 ticks/sec)

    override fun onInitializeClient() {
        LOGGER.info("═══════════════════════════════════════════════════════")
        LOGGER.info("  $MOD_NAME v$VERSION")
        LOGGER.info("  Initializing client-side utilities...")
        LOGGER.info("═══════════════════════════════════════════════════════")

        // Register network packet receivers
        try {
            NetworkHandler.registerReceivers()
            LOGGER.info("✓ Network handlers registered successfully")
        } catch (e: Exception) {
            LOGGER.error("✗ Failed to register network handlers", e)
            return
        }

        // Register tick event for cleanup
        ClientTickEvents.END_CLIENT_TICK.register { client ->
            tickCounter++
            if (tickCounter >= CLEANUP_INTERVAL_TICKS) {
                tickCounter = 0
                MusicPlayer.cleanupFinishedTracks()
            }
        }

        LOGGER.info("═══════════════════════════════════════════════════════")
        LOGGER.info("  $MOD_NAME initialized successfully!")
        LOGGER.info("  Ready to receive packets from compatible servers")
        LOGGER.info("═══════════════════════════════════════════════════════")
    }
}
