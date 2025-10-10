package guidebook.api.data.page;

import com.google.gson.JsonObject;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

import guidebook.api.data.AbstractPageBuilder;
import guidebook.api.data.EntryBuilder;
import guidebook.api.data.util.ItemStackHelper;

public class SpotlightPageBuilder extends AbstractPageBuilder<SpotlightPageBuilder> {
    private final String item;
    private String title;
    private Boolean linkRecipe;
    private String text;

    public SpotlightPageBuilder(ItemStack stack, EntryBuilder parent, HolderLookup.Provider provider) {
        super("guidebook:spotlight", parent);
        this.item = ItemStackHelper.serializeStack(stack, provider);
    }

    @Override
    protected void serialize(JsonObject json) {
        json.addProperty("item", item);

        if (title != null) {
            json.addProperty("title", title);
        }
        if (linkRecipe != null) {
            json.addProperty("link_recipe", linkRecipe);
        }
        if (text != null) {
            json.addProperty("text", text);
        }
    }

    public SpotlightPageBuilder setTitle(String title) {
        this.title = title;

        return this;
    }

    public SpotlightPageBuilder setLinkRecipe(Boolean linkRecipe) {
        this.linkRecipe = linkRecipe;

        return this;
    }

    public SpotlightPageBuilder setText(String text) {
        this.text = text;

        return this;
    }

}
