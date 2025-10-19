package handbook.api.data.page;

import net.minecraft.resources.ResourceLocation;

import handbook.api.data.EntryBuilder;

public class StonecuttingPageBuilder extends RecipePageBuilder<StonecuttingPageBuilder> {

    public StonecuttingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("handbook:stonecutting", recipe, entryBuilder);
    }

}
