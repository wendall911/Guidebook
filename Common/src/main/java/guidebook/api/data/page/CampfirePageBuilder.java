package guidebook.api.data.page;

import net.minecraft.resources.ResourceLocation;

import guidebook.api.data.EntryBuilder;

public class CampfirePageBuilder extends RecipePageBuilder<CampfirePageBuilder> {

    public CampfirePageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("guidebook:campfire", recipe, entryBuilder);
    }

}
