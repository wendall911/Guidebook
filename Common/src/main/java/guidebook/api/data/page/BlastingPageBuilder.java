package guidebook.api.data.page;

import net.minecraft.resources.ResourceLocation;

import guidebook.api.data.EntryBuilder;

public class BlastingPageBuilder extends RecipePageBuilder<BlastingPageBuilder> {

    public BlastingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("guidebook:blasting", recipe, entryBuilder);
    }

}
