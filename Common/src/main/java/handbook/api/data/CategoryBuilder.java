package handbook.api.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;

import handbook.api.data.util.ItemStackHelper;

public class CategoryBuilder {

    private final BookBuilder bookBuilder;
    private final Identifier id;
    private final String name;
    private final String description;
    private final String icon;
    private final List<EntryBuilder> entries = new ArrayList<>();
    private String parent;
    private String flag;
    private Integer sortnum;
    private Boolean secret;

    protected CategoryBuilder(String id, String name, String description,
                              ItemStackTemplate icon, BookBuilder bookBuilder) {
        this(
            id,
            name,
            description,
            ItemStackHelper.serializeStack(icon),
            bookBuilder
        );
    }

    protected CategoryBuilder(String id, String name, String description,
            String icon, BookBuilder bookBuilder) {
        this.bookBuilder = bookBuilder;
        this.id = Identifier.fromNamespaceAndPath(bookBuilder.getId().getNamespace(), id);
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("description", description);
        json.addProperty("icon", icon);

        if (parent != null) {
            json.addProperty("parent", parent);
        }
        if (flag != null) {
            json.addProperty("flag", flag);
        }
        if (sortnum != null) {
            json.addProperty("sortnum", sortnum);
        }
        if (secret != null) {
            json.addProperty("secret", secret);
        }

        this.serialize(json);

        return json;
    }

    protected void serialize(JsonObject json) {}

    protected List<EntryBuilder> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public BookBuilder build() {
        return bookBuilder;
    }

    public EntryBuilder addEntry(String id, String name, String icon) {
        return this.addEntry(new EntryBuilder(id, name, icon, this));
    }

    public EntryBuilder addEntry(String id, String name, ItemStackTemplate icon) {
        return this.addEntry(new EntryBuilder(id, name, icon, this));
    }

    protected EntryBuilder addEntry(EntryBuilder builder) {
        this.entries.add(builder);

        return builder;
    }

    public CategoryBuilder setParent(String parent) {
        this.parent = bookBuilder.getId().getNamespace() + ":" + parent;

        return this;
    }

    public CategoryBuilder setParent(CategoryBuilder parent) {
        this.parent = parent.getId().toString();

        return this;
    }

    public CategoryBuilder setFlag(String flag) {
        this.flag = flag;

        return this;
    }

    public CategoryBuilder setSortnum(Integer sortnum) {
        this.sortnum = sortnum;

        return this;
    }

    public CategoryBuilder setSecret(Boolean secret) {
        this.secret = secret;

        return this;
    }

    protected Identifier getId() {
        return id;
    }

}
