package guidebook.api.data.page;

import net.minecraft.resources.ResourceLocation;

import guidebook.api.data.EntryBuilder;

public class StonecuttingPageBuilder extends RecipePageBuilder<StonecuttingPageBuilder> {

    public StonecuttingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("guidebook:stonecutting", recipe, entryBuilder);
    }

}
