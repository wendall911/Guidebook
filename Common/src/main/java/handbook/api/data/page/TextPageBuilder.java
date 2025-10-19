package handbook.api.data.page;

import com.google.gson.JsonObject;

import handbook.api.data.AbstractPageBuilder;
import handbook.api.data.EntryBuilder;

public class TextPageBuilder extends AbstractPageBuilder<TextPageBuilder> {

    private final String text;
    private final String title;

    public TextPageBuilder(String text, String title, EntryBuilder entryBuilder) {
        super("handbook:text", entryBuilder);
        this.text = text;
        this.title = title;
    }

    public TextPageBuilder(String text, EntryBuilder entryBuilder) {
        super("handbook:text", entryBuilder);
        this.text = text;
        this.title = null;
    }

    @Override
    protected void serialize(JsonObject json) {
        json.addProperty("text", text);

        if (title != null) {
            json.addProperty("title", title);
        }
    }

}
