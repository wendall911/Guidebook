package handbook.mixin;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapelessRecipe.class)
public interface AccessorShapelessRecipe {

    @Accessor
    List<Ingredient> getIngredients();

    @Accessor
    ItemStack getResult();

}
