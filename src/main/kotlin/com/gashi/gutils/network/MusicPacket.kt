package com.gashi.gutils.network

import net.minecraft.network.PacketByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier

/**
 * Custom packet for sending music playback commands from server to client
 *
 * Packet structure:
 * - action: PLAY, STOP, or STOP_ALL
 * - musicId: Resource identifier for the music (nullable for STOP_ALL)
 * - volume: Volume level (0.0 to 1.0+)
 * - pitch: Pitch multiplier (0.5 to 2.0)
 * - loop: Whether to loop the music
 */
data class MusicPacket(
    val action: Action,
    val musicId: Identifier?,
    val volume: Float,
    val pitch: Float,
    val loop: Boolean
) : CustomPayload {

    enum class Action {
        PLAY,
        STOP,
        STOP_ALL
    }

    companion object {
        val ID = CustomPayload.Id<MusicPacket>(
            Identifier.of("cobbleranked", "music")
        )

        val CODEC: PacketCodec<PacketByteBuf, MusicPacket> = PacketCodec.of(
            { packet, buf -> packet.write(buf) },
            { buf -> read(buf) }
        )

        /**
         * Create a PLAY packet
         */
        fun play(musicId: Identifier, volume: Float = 1.0f, pitch: Float = 1.0f, loop: Boolean = true): MusicPacket {
            return MusicPacket(Action.PLAY, musicId, volume, pitch, loop)
        }

        /**
         * Create a STOP packet for specific music
         */
        fun stop(musicId: Identifier): MusicPacket {
            return MusicPacket(Action.STOP, musicId, 1.0f, 1.0f, false)
        }

        /**
         * Create a STOP_ALL packet
         */
        fun stopAll(): MusicPacket {
            return MusicPacket(Action.STOP_ALL, null, 1.0f, 1.0f, false)
        }

        /**
         * Read packet from buffer
         */
        private fun read(buf: PacketByteBuf): MusicPacket {
            val action = Action.valueOf(buf.readString())
            val hasMusicId = buf.readBoolean()
            val musicId = if (hasMusicId) Identifier.of(buf.readString()) else null
            val volume = buf.readFloat()
            val pitch = buf.readFloat()
            val loop = buf.readBoolean()

            return MusicPacket(action, musicId, volume, pitch, loop)
        }
    }

    /**
     * Write packet to buffer
     */
    private fun write(buf: PacketByteBuf) {
        buf.writeString(action.name)
        buf.writeBoolean(musicId != null)
        if (musicId != null) {
            buf.writeString(musicId.toString())
        }
        buf.writeFloat(volume)
        buf.writeFloat(pitch)
        buf.writeBoolean(loop)
    }

    override fun getId(): CustomPayload.Id<out CustomPayload> = ID
}
