package handbook.api.data.page;

import net.minecraft.resources.ResourceLocation;

import handbook.api.data.EntryBuilder;

public class BlastingPageBuilder extends RecipePageBuilder<BlastingPageBuilder> {

    public BlastingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("handbook:blasting", recipe, entryBuilder);
    }

}
