package com.gashi.gutils.unitrademarket.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record PriceInputRequestPayload(String promptMessage) implements CustomPayload {
    public static final CustomPayload.Id<PriceInputRequestPayload> ID =
        new CustomPayload.Id<>(NetworkConstants.REQUEST_PRICE_INPUT);

    public static final PacketCodec<PacketByteBuf, PriceInputRequestPayload> CODEC =
        PacketCodec.of(PriceInputRequestPayload::write, PriceInputRequestPayload::new);

    public PriceInputRequestPayload(PacketByteBuf buf) {
        this(buf.readString());
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(promptMessage);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
