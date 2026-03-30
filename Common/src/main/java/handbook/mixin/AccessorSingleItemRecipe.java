package handbook.mixin;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SingleItemRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SingleItemRecipe.class)
public interface AccessorSingleItemRecipe {

    @Accessor
    Ingredient getInput();

    @Accessor
    ItemStackTemplate getResult();

}
