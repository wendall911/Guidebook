package handbook.client.base;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import handbook.mixin.AccessorRecipeManager;
import handbook.platform.Services;

public class RecipeData {

    private static final Map<Identifier, RecipeHolder<?>> RECIPE_HOLDERS = new HashMap<>();

    public static RecipeHolder<?> getRecipeHolder(Identifier id, RecipeType<?> recipeType) {
        if (RECIPE_HOLDERS.containsKey(id)) {
            return RECIPE_HOLDERS.get(id);
        }
        else if (Minecraft.getInstance().getSingleplayerServer() != null) {
            return getRecipeHolderClient(id, recipeType);
        }
        else {
            return getRecipeHolderServer(id, recipeType);
        }
    }

    private static RecipeHolder<?> getRecipeHolderClient(Identifier id,  RecipeType<?> recipeType) {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();

        if (server != null) {
            AccessorRecipeManager manager = (AccessorRecipeManager) server.getRecipeManager();

            RecipeHolder<?> recipeHolder = manager.getRecipes().values().stream().filter(
                holder -> holder.id().identifier().equals(id) && holder.value().getType() == recipeType
            ).findFirst().orElse(null);

            if (recipeHolder != null) {
                setRecipe(id, recipeHolder);

                return recipeHolder;
            }
        }

        return null;
    }

    private static RecipeHolder<?> getRecipeHolderServer(Identifier id, RecipeType<?> recipeType)  {
        RecipeHolder<?> recipeHolder = RECIPE_HOLDERS.get(id);

        if (recipeHolder == null) {
            Services.BOOK_HELPER.fetchRecipe(id);
        }

        if (recipeHolder != null && recipeHolder.value().getType() == recipeType) {
            return recipeHolder;
        }
        else {
            return null;
        }
    }

    public static void setRecipe(Identifier id, RecipeHolder<?> recipeHolder) {
        RECIPE_HOLDERS.put(id, recipeHolder);
    }

}
