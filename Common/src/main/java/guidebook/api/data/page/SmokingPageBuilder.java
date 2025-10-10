package guidebook.api.data.page;

import net.minecraft.resources.ResourceLocation;

import guidebook.api.data.EntryBuilder;

public class SmokingPageBuilder extends RecipePageBuilder<SmokingPageBuilder> {

    public SmokingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("guidebook:smoking", recipe, entryBuilder);
    }

}
