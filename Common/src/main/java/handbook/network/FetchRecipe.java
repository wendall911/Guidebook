package handbook.network;

import org.jspecify.annotations.NonNull;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import static handbook.api.HandbookAPI.prefix;

public record FetchRecipe(Identifier recipeId) implements CustomPacketPayload {

    public static final Identifier ID = prefix("fetch_recipe");
    public static final Type<FetchRecipe> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, FetchRecipe> STREAM_CODEC = StreamCodec.composite(
        Identifier.STREAM_CODEC,
        FetchRecipe::recipeId,
        FetchRecipe::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
