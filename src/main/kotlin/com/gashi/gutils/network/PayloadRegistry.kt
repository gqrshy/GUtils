package com.gashi.gutils.network

import com.gashi.gutils.GUtils
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry

/**
 * Network payload registration for GUtils client
 *
 * Registers all custom packet types with Fabric's PayloadTypeRegistry.
 * Required for Minecraft 1.21+ to properly encode/decode CustomPayload packets.
 */
object PayloadRegistry {

    /**
     * Register all payload types
     * Called during mod initialization
     */
    fun register() {
        try {
            // Register client→server payloads (C2S)
            PayloadTypeRegistry.playC2S().register(
                ClientCapabilityPayload.ID,
                ClientCapabilityPayload.CODEC
            )

            GUtils.LOGGER.info("[PayloadRegistry] Registered ClientCapabilityPayload (C2S)")

            // Register server→client payloads (S2C) for receiving
            PayloadTypeRegistry.playS2C().register(
                MusicPacket.ID,
                MusicPacket.CODEC
            )

            GUtils.LOGGER.info("[PayloadRegistry] Registered MusicPacket (S2C)")

        } catch (e: Exception) {
            GUtils.LOGGER.error("[PayloadRegistry] Failed to register payload types", e)
            throw e
        }
    }
}
