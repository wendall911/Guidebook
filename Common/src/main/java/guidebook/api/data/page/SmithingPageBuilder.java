package guidebook.api.data.page;

import net.minecraft.resources.ResourceLocation;

import guidebook.api.data.EntryBuilder;

public class SmithingPageBuilder extends RecipePageBuilder<SmithingPageBuilder> {

    public SmithingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("guidebook:smithing", recipe, entryBuilder);
    }

}
