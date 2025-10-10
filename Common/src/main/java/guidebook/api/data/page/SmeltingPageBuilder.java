package guidebook.api.data.page;

import net.minecraft.resources.ResourceLocation;

import guidebook.api.data.EntryBuilder;

public class SmeltingPageBuilder extends RecipePageBuilder<SmeltingPageBuilder> {

    public SmeltingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("guidebook:smelting", recipe, entryBuilder);
    }

}
