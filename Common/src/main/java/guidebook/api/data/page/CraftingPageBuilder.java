package guidebook.api.data.page;

import net.minecraft.resources.ResourceLocation;

import guidebook.api.data.EntryBuilder;

public class CraftingPageBuilder extends RecipePageBuilder<CraftingPageBuilder> {

    public CraftingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("guidebook:crafting", recipe, entryBuilder);
    }

}
