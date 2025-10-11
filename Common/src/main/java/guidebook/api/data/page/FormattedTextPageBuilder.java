package guidebook.api.data.page;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import guidebook.api.data.AbstractPageBuilder;
import guidebook.api.data.EntryBuilder;

public class FormattedTextPageBuilder extends AbstractPageBuilder<FormattedTextPageBuilder> {

    private final String translate;
    private final Object[] with;

    public FormattedTextPageBuilder(String translate, Object[] with, EntryBuilder parent) {
        super("guidebook:text", parent);
        this.translate = translate;
        this.with = with;
    }

    @Override
    protected void serialize(JsonObject json) {
        JsonElement text = new JsonObject();
        JsonArray withArray = new JsonArray();

        text.getAsJsonObject().addProperty("translate", translate);

        if (with != null && with.length > 0) {
            for (Object obj : with) {
                switch (obj) {
                    case String string -> withArray.add(string);
                    case String[] strings -> {
                        JsonArray arr = new JsonArray();

                        for (String s : strings) {
                            arr.add(s);
                        }

                        withArray.add(arr);
                    }
                    case JsonElement jsonElement -> withArray.add(jsonElement);
                    default ->
                        throw new IllegalArgumentException("Unsupported type in 'with' array: " + obj.getClass());
                }
            }

            text.getAsJsonObject().add("with", withArray);
        }

        json.add("text", text);
    }

}
