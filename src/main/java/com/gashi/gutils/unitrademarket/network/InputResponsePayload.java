package com.gashi.gutils.unitrademarket.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record InputResponsePayload(String inputType, String value) implements CustomPayload {
    public static final CustomPayload.Id<InputResponsePayload> ID =
        new CustomPayload.Id<>(NetworkConstants.INPUT_RESPONSE_PACKET);

    public static final PacketCodec<PacketByteBuf, InputResponsePayload> CODEC =
        PacketCodec.of(InputResponsePayload::write, InputResponsePayload::new);

    public InputResponsePayload(PacketByteBuf buf) {
        this(buf.readString(), buf.readString());
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(inputType);
        buf.writeString(value);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
