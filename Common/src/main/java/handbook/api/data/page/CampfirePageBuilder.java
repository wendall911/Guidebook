package handbook.api.data.page;

import net.minecraft.resources.Identifier;

import handbook.api.data.EntryBuilder;

public class CampfirePageBuilder extends RecipePageBuilder<CampfirePageBuilder> {

    public CampfirePageBuilder(Identifier recipe, EntryBuilder entryBuilder) {
        super("handbook:campfire", recipe, entryBuilder);
    }

}
