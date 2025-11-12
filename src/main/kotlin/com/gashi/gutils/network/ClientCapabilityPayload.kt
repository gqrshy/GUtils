package com.gashi.gutils.network

import com.gashi.gutils.GUtils
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

/**
 * Payload sent from GUtils client to CobbleRanked server to indicate client capabilities.
 * This allows the server to enable features like custom music for clients with GUtils installed.
 */
data class ClientCapabilityPayload(
    val hasGUtilsMod: Boolean,
    val modVersion: String
) : CustomPayload {

    companion object {
        val ID = CustomPayload.Id<ClientCapabilityPayload>(
            Identifier.of("cobbleranked", "client_capability")
        )

        val CODEC: PacketCodec<RegistryByteBuf, ClientCapabilityPayload> =
            PacketCodec.of(
                { payload, buf -> payload.write(buf) },
                { buf -> read(buf) }
            )

        fun read(buf: RegistryByteBuf): ClientCapabilityPayload {
            val hasGUtilsMod = buf.readBoolean()
            val modVersion = buf.readString()
            return ClientCapabilityPayload(hasGUtilsMod, modVersion)
        }
    }

    override fun getId(): CustomPayload.Id<out CustomPayload> = ID

    fun write(buf: RegistryByteBuf) {
        buf.writeBoolean(hasGUtilsMod)
        buf.writeString(modVersion)
    }
}
