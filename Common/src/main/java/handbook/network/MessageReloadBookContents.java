package handbook.network;

import org.jetbrains.annotations.NotNull;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import handbook.api.HandbookAPI;

public record MessageReloadBookContents() implements CustomPacketPayload {

    public static final Identifier ID = HandbookAPI.prefix("reload_books");
    public static final StreamCodec<@NotNull FriendlyByteBuf, @NotNull MessageReloadBookContents> CODEC = StreamCodec.unit(
        new MessageReloadBookContents()
    );
    public static final Type<@NotNull MessageReloadBookContents> TYPE = new Type<>(ID);

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }

}
