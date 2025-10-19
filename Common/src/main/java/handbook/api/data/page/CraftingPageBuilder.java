package handbook.api.data.page;

import net.minecraft.resources.ResourceLocation;

import handbook.api.data.EntryBuilder;

public class CraftingPageBuilder extends RecipePageBuilder<CraftingPageBuilder> {

    public CraftingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("handbook:crafting", recipe, entryBuilder);
    }

}
