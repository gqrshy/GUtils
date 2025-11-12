package com.gashi.gutils.unitrademarket.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record SearchInputRequestPayload(String promptMessage) implements CustomPayload {
    public static final CustomPayload.Id<SearchInputRequestPayload> ID =
        new CustomPayload.Id<>(NetworkConstants.REQUEST_SEARCH_INPUT);

    public static final PacketCodec<PacketByteBuf, SearchInputRequestPayload> CODEC =
        PacketCodec.of(SearchInputRequestPayload::write, SearchInputRequestPayload::new);

    public SearchInputRequestPayload(PacketByteBuf buf) {
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
