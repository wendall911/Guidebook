package guidebook.api.data.page;

import com.google.gson.JsonObject;
import guidebook.api.data.AbstractPageBuilder;
import guidebook.api.data.EntryBuilder;

public class EmptyPageBuilder extends AbstractPageBuilder<EmptyPageBuilder> {
    private final boolean drawFiller;

    public EmptyPageBuilder(boolean drawFiller, EntryBuilder entryBuilder) {
        super("guidebook:empty", entryBuilder);
        this.drawFiller = drawFiller;
    }

    @Override
    protected void serialize(JsonObject json) {
        json.addProperty("draw_filler", drawFiller);
    }

}
