package handbook.common.util;

import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.RecipeHolder;

import handbook.mixin.AccessorRecipeManager;
import handbook.network.FetchRecipe;
import handbook.platform.Services;

public class ServerRecipeUtil {

    public static void processFetchRecipe(FetchRecipe data, ServerPlayer player) {
        MinecraftServer server = player.level().getServer();
        AccessorRecipeManager manager = (AccessorRecipeManager) server.getRecipeManager();
        Identifier id = data.recipeId();

        RecipeHolder<?> recipeHolder = manager.getRecipes().values().stream().filter(
            holder -> holder.id().identifier().equals(id)
        ).findFirst().orElse(null);

        if (recipeHolder != null) {
            Services.BOOK_HELPER.sendRecipe(player, id, recipeHolder);
        }
    }

}
