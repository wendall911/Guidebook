package handbook.api.data.page;

import net.minecraft.resources.Identifier;

import handbook.api.data.EntryBuilder;

public class StonecuttingPageBuilder extends RecipePageBuilder<StonecuttingPageBuilder> {

    public StonecuttingPageBuilder(Identifier recipe, EntryBuilder entryBuilder) {
        super("handbook:stonecutting", recipe, entryBuilder);
    }

}
