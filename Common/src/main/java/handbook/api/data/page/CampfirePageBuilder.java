package handbook.api.data.page;

import net.minecraft.resources.ResourceLocation;

import handbook.api.data.EntryBuilder;

public class CampfirePageBuilder extends RecipePageBuilder<CampfirePageBuilder> {

    public CampfirePageBuilder(ResourceLocation recipe, EntryBuilder entryBuilder) {
        super("handbook:campfire", recipe, entryBuilder);
    }

}
