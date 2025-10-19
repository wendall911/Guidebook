package handbook.api.data.page;

import net.minecraft.resources.ResourceLocation;

import handbook.api.data.EntryBuilder;

public class SmeltingPageBuilder extends RecipePageBuilder<SmeltingPageBuilder> {

    public SmeltingPageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("handbook:smelting", recipe, entryBuilder);
    }

}
