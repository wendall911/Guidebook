package handbook.api.data.page;

import net.minecraft.resources.ResourceLocation;

import handbook.api.data.EntryBuilder;

public class SmithingPageBuilder extends RecipePageBuilder<SmithingPageBuilder> {

    public SmithingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("handbook:smithing", recipe, entryBuilder);
    }

}
