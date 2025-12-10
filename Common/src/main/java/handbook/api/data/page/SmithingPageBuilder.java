package handbook.api.data.page;

import net.minecraft.resources.Identifier;

import handbook.api.data.EntryBuilder;

public class SmithingPageBuilder extends RecipePageBuilder<SmithingPageBuilder> {

    public SmithingPageBuilder(Identifier recipe, EntryBuilder entryBuilder) {
        super("handbook:smithing", recipe, entryBuilder);
    }

}
