package handbook.network;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import handbook.api.HandbookAPI;

public record MessageReloadBookContents() implements CustomPacketPayload {

    public static final ResourceLocation ID = HandbookAPI.prefix("reload_books");
    public static final StreamCodec<FriendlyByteBuf, MessageReloadBookContents> CODEC = StreamCodec.unit(
        new MessageReloadBookContents()
    );
    public static final Type<MessageReloadBookContents> TYPE = new Type<>(ID);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
