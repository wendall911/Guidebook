package handbook.api.data.page;

import net.minecraft.resources.Identifier;

import handbook.api.data.EntryBuilder;

public class SmokingPageBuilder extends RecipePageBuilder<SmokingPageBuilder> {

    public SmokingPageBuilder(Identifier recipe, EntryBuilder entryBuilder) {
        super("handbook:smoking", recipe, entryBuilder);
    }

}
