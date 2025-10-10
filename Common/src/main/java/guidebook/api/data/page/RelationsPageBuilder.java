package guidebook.api.data.page;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;

import guidebook.api.data.AbstractPageBuilder;
import guidebook.api.data.EntryBuilder;

public class RelationsPageBuilder extends AbstractPageBuilder<RelationsPageBuilder> {
    private final List<ResourceLocation> entries = new ArrayList<>();
    private String title;
    private String text;

    public RelationsPageBuilder(EntryBuilder entryBuilder) {
        super("guidebook:relations", entryBuilder);
    }

    @Override
    protected void serialize(JsonObject json) {
        JsonArray entries = new JsonArray();
        for (ResourceLocation entry : this.entries) {
            entries.add(entry.toString());
        }
        json.add("entries", entries);

        if (title != null) {
            json.addProperty("title", title);
        }
        if (text != null) {
            json.addProperty("text", text);
        }
    }

    public RelationsPageBuilder addEntry(ResourceLocation entry) {
        entries.add(entry);
        return this;
    }

    public RelationsPageBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public RelationsPageBuilder setText(String text) {
        this.text = text;
        return this;
    }
}
