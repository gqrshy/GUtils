package com.gashi.gutils.unitrademarket.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record QuantityInputRequestPayload(String promptMessage, int maxQuantity) implements CustomPayload {
    public static final CustomPayload.Id<QuantityInputRequestPayload> ID =
        new CustomPayload.Id<>(NetworkConstants.REQUEST_QUANTITY_INPUT);

    public static final PacketCodec<PacketByteBuf, QuantityInputRequestPayload> CODEC =
        PacketCodec.of(QuantityInputRequestPayload::write, QuantityInputRequestPayload::new);

    public QuantityInputRequestPayload(PacketByteBuf buf) {
        this(buf.readString(), buf.readInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(promptMessage);
        buf.writeInt(maxQuantity);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
