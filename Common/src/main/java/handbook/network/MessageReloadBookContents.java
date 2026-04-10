package handbook.network;

import org.jspecify.annotations.NonNull;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import handbook.api.HandbookAPI;

public record MessageReloadBookContents() implements CustomPacketPayload {

    public static final Identifier ID = HandbookAPI.prefix("reload_books");
    public static final StreamCodec<@NonNull FriendlyByteBuf, @NonNull MessageReloadBookContents> CODEC = StreamCodec.unit(
        new MessageReloadBookContents()
    );
    public static final Type<@NonNull MessageReloadBookContents> TYPE = new Type<>(ID);

    @Override
    public @NonNull Type<? extends @NonNull CustomPacketPayload> type() {
        return TYPE;
    }

}
