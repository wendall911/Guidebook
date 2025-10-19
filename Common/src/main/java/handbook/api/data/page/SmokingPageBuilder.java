package handbook.api.data.page;

import net.minecraft.resources.ResourceLocation;

import handbook.api.data.EntryBuilder;

public class SmokingPageBuilder extends RecipePageBuilder<SmokingPageBuilder> {

    public SmokingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("handbook:smoking", recipe, entryBuilder);
    }

}
