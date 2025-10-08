package guidebook.client.book.page;

import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;

import guidebook.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageSmelting extends PageSimpleProcessingRecipe<SmeltingRecipe> {

	public PageSmelting() {
		super(RecipeType.SMELTING);
	}

}
