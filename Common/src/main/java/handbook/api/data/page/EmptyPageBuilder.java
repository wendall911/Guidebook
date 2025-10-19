package handbook.api.data.page;

import com.google.gson.JsonObject;
import handbook.api.data.AbstractPageBuilder;
import handbook.api.data.EntryBuilder;

public class EmptyPageBuilder extends AbstractPageBuilder<EmptyPageBuilder> {
    private final boolean drawFiller;

    public EmptyPageBuilder(boolean drawFiller, EntryBuilder entryBuilder) {
        super("handbook:empty", entryBuilder);
        this.drawFiller = drawFiller;
    }

    @Override
    protected void serialize(JsonObject json) {
        json.addProperty("draw_filler", drawFiller);
    }

}
