package handbook.network;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import handbook.api.HandbookAPI;

public record MessageOpenBookGui(Identifier book, @Nullable Identifier entry, int page) implements CustomPacketPayload {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(HandbookAPI.MODID, "open_book");
    public static final StreamCodec<@NotNull FriendlyByteBuf, @NotNull MessageOpenBookGui> CODEC = StreamCodec.composite(
        Identifier.STREAM_CODEC,
        MessageOpenBookGui::book,
        ByteBufCodecs.STRING_UTF8.map(
            entry -> entry.isEmpty() ? null :
                Identifier.tryParse(entry), entry -> entry == null ? "" : entry.toString()
        ),
        MessageOpenBookGui::entry,
        ByteBufCodecs.VAR_INT,
        MessageOpenBookGui::page,
        MessageOpenBookGui::new
    );
    public static final Type<@NotNull MessageOpenBookGui> TYPE = new Type<>(ID);

    @Override
    public @NotNull Type<@NotNull ? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
