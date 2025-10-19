package handbook.mixin;

import java.util.Optional;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTransformRecipe.class)
public interface AccessorSmithingTransformRecipe {

    @Accessor
    Optional<Ingredient> getTemplate();

    @Accessor
    Ingredient getBase();

    @Accessor
    Optional<Ingredient> getAddition();

}
