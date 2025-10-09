package guidebook.client.book.page;

import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

import guidebook.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageBlasting extends PageSimpleProcessingRecipe<BlastingRecipe> {

    public PageBlasting() {
        super(RecipeType.BLASTING);
    }

}
