package handbook.client.book.page;

import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;

import handbook.client.book.page.abstr.PageSimpleProcessingRecipe;

public class PageCampfireCooking extends PageSimpleProcessingRecipe<CampfireCookingRecipe> {

    public PageCampfireCooking() {
        super(RecipeType.CAMPFIRE_COOKING);
    }

}
