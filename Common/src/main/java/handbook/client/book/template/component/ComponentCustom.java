package handbook.client.book.template.component;

import java.util.function.UnaryOperator;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;

import handbook.api.ICustomComponent;
import handbook.api.IVariable;
import handbook.client.book.BookContentsBuilder;
import handbook.client.book.BookEntry;
import handbook.client.book.BookPage;
import handbook.client.book.gui.GuiBookEntry;
import handbook.client.book.template.TemplateComponent;
import handbook.common.util.SerializationUtil;

public class ComponentCustom extends TemplateComponent {

    @SerializedName("class") String clazz;

    private transient ICustomComponent callbacks;

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
        super.onVariablesAvailable(lookup, registries);
        try {
            Class<?> classObj = Class.forName(clazz);
            callbacks = (ICustomComponent) SerializationUtil.RAW_GSON.fromJson(sourceObject, classObj);
            callbacks.onVariablesAvailable(lookup, registries);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create custom component " + clazz, e);
        }
    }

    @Override
    public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
        callbacks.build(x, y, pageNum);
    }

    @Override
    public void render(GuiGraphics graphics, BookPage page, int mouseX, int mouseY, float pticks) {
        callbacks.render(graphics, page.parent, pticks, mouseX, mouseY);
    }

    @Override
    public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
        callbacks.onDisplayed(parent);
    }

    @Override
    public boolean mouseClicked(BookPage page, double mouseX, double mouseY, int mouseButton) {
        return callbacks.mouseClicked(page.parent, mouseX, mouseY, mouseButton);
    }

}
