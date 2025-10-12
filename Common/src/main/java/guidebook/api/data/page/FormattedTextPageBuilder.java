package guidebook.api.data.page;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import guidebook.api.data.AbstractPageBuilder;
import guidebook.api.data.EntryBuilder;

public class FormattedTextPageBuilder extends AbstractPageBuilder<FormattedTextPageBuilder> {

    private final String translate;
    private JsonArray with = new JsonArray();

    public FormattedTextPageBuilder(String translate, EntryBuilder parent) {
        super("guidebook:text", parent);
        this.translate = translate;
    }

    @Override
    protected void serialize(JsonObject json) {
        JsonElement text = new JsonObject();

        text.getAsJsonObject().addProperty("translate", translate);
        text.getAsJsonObject().add("with", with);

        json.add("text", text);
    }

    public FormattedTextPageBuilder with(String key, String value) {
        JsonElement element = new JsonObject();

        element.getAsJsonObject().addProperty(key, value);

        with.add(element);

        return this;
    }

    public FormattedTextPageBuilder with(JsonElement element) {
        with.add(element);

        return this;
    }

    public FormattedTextPageBuilder with(Object object) {
        switch (object) {
            case String s -> with.add(s);
            case Number n -> with.add(n);
            case Boolean b -> with.add(b);
            case Character c -> with.add(c);
            default -> {
                throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
            }
        }

        return this;
    }

    public FormattedTextPageBuilder with(String... values) {
        JsonArray array = new JsonArray();

        for (String value : values) {
            array.add(value);
        }

        with.add(array);

        return this;
    }

}
