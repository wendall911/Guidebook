package guidebook.api.data.page;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import guidebook.api.data.AbstractPageBuilder;
import guidebook.api.data.EntryBuilder;

public class QuestPageBuilder extends AbstractPageBuilder<QuestPageBuilder> {

    private final ResourceLocation trigger;
    private String title;
    private String text;

    public QuestPageBuilder(EntryBuilder entryBuilder, ResourceLocation trigger) {
        super("guidebook:quest", entryBuilder);
        this.trigger = trigger;
    }

    @Override
    protected void serialize(JsonObject json) {
        if (trigger != null) {
            json.addProperty("trigger", trigger.toString());
        }
        if (title != null) {
            json.addProperty("title", title);
        }
        if (text != null) {
            json.addProperty("text", text);
        }
    }

    public QuestPageBuilder setTitle(String title) {
        this.title = title;

        return this;
    }

    public QuestPageBuilder setText(String text) {
        this.text = text;

        return this;
    }

}
