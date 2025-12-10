package handbook.api.data.page;

import net.minecraft.resources.Identifier;

import handbook.api.data.EntryBuilder;

public class BlastingPageBuilder extends RecipePageBuilder<BlastingPageBuilder> {

    public BlastingPageBuilder(Identifier recipe, EntryBuilder entryBuilder) {
        super("handbook:blasting", recipe, entryBuilder);
    }

}
