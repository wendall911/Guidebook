package handbook.api.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;

public class TemplateBuilder {

    private final BookBuilder bookBuilder;
    private final ResourceLocation id;
    private final JsonArray components = new JsonArray();
    private final JsonArray includes = new JsonArray();

    protected TemplateBuilder(String id, BookBuilder bookBuilder) {
        this.bookBuilder = bookBuilder;
        this.id = ResourceLocation.fromNamespaceAndPath(bookBuilder.getId().getNamespace(), id);
    }

    JsonObject toJson() {
        JsonObject json = new JsonObject();

        if (!includes.isEmpty()) {
            json.add("include", includes);
        }

        json.add("components", components);

        return json;
    }

    public BookBuilder build() {
        return bookBuilder;
    }

    public ResourceLocation getId() {
        return id;
    }

    public TemplateBuilder addComponent(JsonObject component) {
        components.add(component);

        return this;
    }

    public TemplateBuilder addComponent(JsonElement component) {
        return addComponent(component.getAsJsonObject());
    }

    public TemplateBuilder addInclude(JsonObject include) {
        includes.add(include);

        return this;
    }

    public TemplateBuilder addInclude(JsonElement include) {
        return addInclude(include.getAsJsonObject());
    }

}
