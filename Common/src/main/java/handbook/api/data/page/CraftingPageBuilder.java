package handbook.api.data.page;

import net.minecraft.resources.Identifier;

import handbook.api.data.EntryBuilder;

public class CraftingPageBuilder extends RecipePageBuilder<CraftingPageBuilder> {

    public CraftingPageBuilder(Identifier recipe, EntryBuilder entryBuilder) {
        super("handbook:crafting", recipe, entryBuilder);
    }

}
