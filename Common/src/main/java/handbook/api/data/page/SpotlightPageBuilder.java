package handbook.api.data.page;

import com.google.gson.JsonObject;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;

import handbook.api.data.AbstractPageBuilder;
import handbook.api.data.EntryBuilder;
import handbook.api.data.util.ItemStackHelper;
import handbook.api.data.util.TagKeyHelper;

public class SpotlightPageBuilder extends AbstractPageBuilder<SpotlightPageBuilder> {

    private String item;
    private String title;
    private Boolean linkRecipe;
    private String text;

    public SpotlightPageBuilder(EntryBuilder parent, ItemStackTemplate... stacks) {
        super("handbook:spotlight", parent);
        this.item = serializeStacks(stacks);
    }

    @SafeVarargs
    public SpotlightPageBuilder(EntryBuilder parent, TagKey<Item>... tag) {
        super("handbook:spotlight", parent);
        this.item = serializeTagKeys(tag);
    }

    private String serializeStacks(ItemStackTemplate[] stacks) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < stacks.length; i++) {
            sb.append(ItemStackHelper.serializeStack(stacks[i]));
            if (i < stacks.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    private String serializeTagKeys(TagKey<Item>[] tags) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < tags.length; i++) {
            sb.append(TagKeyHelper.serializeTagKey(tags[i]));
            if (i < tags.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
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

    @SafeVarargs
    public final SpotlightPageBuilder addTag(TagKey<Item>... tags) {
        this.item = this.item + "," + serializeTagKeys(tags);

        return this;
    }

    public final SpotlightPageBuilder addItem(ItemStackTemplate... stacks) {
        this.item = this.item + "," + serializeStacks(stacks);

        return this;
    }

}
