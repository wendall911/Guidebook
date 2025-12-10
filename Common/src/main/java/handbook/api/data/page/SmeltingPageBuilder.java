package handbook.api.data.page;

import net.minecraft.resources.Identifier;

import handbook.api.data.EntryBuilder;

public class SmeltingPageBuilder extends RecipePageBuilder<SmeltingPageBuilder> {

    public SmeltingPageBuilder(Identifier recipe, EntryBuilder entryBuilder) {
        super("handbook:smelting", recipe, entryBuilder);
    }

}
