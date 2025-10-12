package guidebook.api.data.page;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import guidebook.api.data.AbstractPageBuilder;
import guidebook.api.data.EntryBuilder;

public class CustomPageBuilder extends AbstractPageBuilder<CustomPageBuilder> {

    private final Map<String, String> includes = new HashMap<>();
    private final Map<String, JsonObject> componentIncludes = new HashMap<>();

    public CustomPageBuilder(String template, EntryBuilder parent) {
        super(template, parent);
    }

    @Override
    protected void serialize(JsonObject json) {
        if (!includes.isEmpty()) {
            for (Map.Entry<String, String> entry : includes.entrySet()) {
                json.addProperty(entry.getKey(), entry.getValue());
            }
        }
        if (!componentIncludes.isEmpty()) {
            for (Map.Entry<String, JsonObject> entry : componentIncludes.entrySet()) {
                json.add(entry.getKey(), entry.getValue());
            }
        }
    }

    public CustomPageBuilder set(String key, String value) {
        includes.put(key, value);

        return this;
    }

    public CustomPageBuilder set(String key, JsonObject value) {
        componentIncludes.put(key, value);

        return this;
    }

    public CustomPageBuilder set(String key, JsonElement value) {
        return set(key, value.getAsJsonObject());
    }

}
