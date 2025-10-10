package guidebook.api.data.page;

import com.google.gson.JsonObject;

import guidebook.api.data.AbstractPageBuilder;
import guidebook.api.data.EntryBuilder;

public class LinkPageBuilder extends AbstractPageBuilder<LinkPageBuilder> {

    private final String url;
    private final String linkText;
    private String text;
    private String title;

    public LinkPageBuilder(String url, String linkText, EntryBuilder entryBuilder) {
        super("guidebook:link", entryBuilder);
        this.url = url;
        this.linkText = linkText;
    }

    public LinkPageBuilder setTitle(String title) {
        this.title = title;

        return this;
    }

    public LinkPageBuilder setText(String text) {
        this.text = text;

        return this;
    }

    @Override
    protected void serialize(JsonObject json) {
        json.addProperty("url", url);
        json.addProperty("link_text", linkText);
        if (text != null) {
            json.addProperty("text", text);
        }
        if (title != null) {
            json.addProperty("title", title);
        }
    }

}
