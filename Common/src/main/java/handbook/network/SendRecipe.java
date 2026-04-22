package handbook.network;

import org.jspecify.annotations.NonNull;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

import static handbook.api.HandbookAPI.prefix;

public record SendRecipe(Identifier recipeId, RecipeHolder<?> recipe) implements CustomPacketPayload {

    public static final Identifier ID = prefix("send_recipe");
    public static final Type<SendRecipe> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SendRecipe> STREAM_CODEC = StreamCodec.composite(
        Identifier.STREAM_CODEC,
        SendRecipe::recipeId,
        RecipeHolder.STREAM_CODEC,
        SendRecipe::recipe,
        SendRecipe::new
    );

     @Override
     public @NonNull Type<? extends CustomPacketPayload> type() {
         return TYPE;
     }

}
