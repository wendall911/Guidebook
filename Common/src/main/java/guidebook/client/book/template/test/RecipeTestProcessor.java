package guidebook.client.book.template.test;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

import technology.roughness.whitenoise.util.ResourceLocationHelper;

import guidebook.api.IComponentProcessor;
import guidebook.api.IVariable;
import guidebook.api.IVariableProvider;
import guidebook.mixin.AccessorRecipeManager;
import guidebook.mixin.AccessorShapedRecipe;
import guidebook.mixin.AccessorShapelessRecipe;

public class RecipeTestProcessor implements IComponentProcessor {

    private Recipe<?> recipe;

    @Override
    public void setup(Level level, IVariableProvider variables) {
        // TODO probably add a recipe serializer?
        ResourceLocation recipeId = ResourceLocationHelper.tryParse(
            variables.get("recipe", level.registryAccess()).asString());

        MinecraftServer server = level.getServer();

        if (server == null) {
            recipe = null;
            return;
        }

        AccessorRecipeManager manager = (AccessorRecipeManager) level.getServer().getRecipeManager();
        RecipeHolder<?> recipeHolder = manager.getRecipes().values().stream().filter(
            holder -> holder.id().location().equals(recipeId)
        ).findFirst().orElse(null);

        recipe = recipeHolder != null ? recipeHolder.value() : null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public IVariable process(Level level, String key) {
        if (key.startsWith("item")) {
            int index = Integer.parseInt(key.substring(4)) - 1;
            Ingredient ingredient = getIngredient(index);
            ItemStack[] stacks = ingredient.items().map(
                itemHolder -> new ItemStack(itemHolder.value())
            ).toArray(ItemStack[]::new);
            ItemStack stack = stacks.length == 0 ? ItemStack.EMPTY : stacks[0];

            return IVariable.from(stack, level.registryAccess());
        }
        else if (key.equals("text")) {
            ItemStack out = getResult();

            return IVariable.wrap(out.getCount() + "x$(br)" + out.getHoverName(), level.registryAccess());
        }
        else if (key.equals("icount")) {
            return IVariable.wrap(getResult().getCount(), level.registryAccess());
        }
        else if (key.equals("iname")) {
            return IVariable.wrap(getResult().getHoverName().getString(), level.registryAccess());
        }

        return null;
    }

    private Ingredient getIngredient(int index) {
        boolean shaped = recipe instanceof ShapedRecipe;

        if (!shaped) {
            return ((AccessorShapelessRecipe) recipe).getIngredients().get(index);
        }
        else {
            List<Ingredient> shapedIngredients = new ArrayList<>();

            ((ShapedRecipe) recipe).getIngredients().forEach(optionalIngredient -> {
                shapedIngredients.add(optionalIngredient.orElse(null));
            });

            return shapedIngredients.get(index);
        }
    }

    private ItemStack getResult() {
        boolean shaped = recipe instanceof ShapedRecipe;

        if (!shaped) {
            return ((AccessorShapelessRecipe) recipe).getResult();
        }
        else {
            return ((AccessorShapedRecipe) recipe).getResult();
        }
    }

}
