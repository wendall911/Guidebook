package handbook.client.book.template.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;

import handbook.api.IVariable;
import handbook.client.book.BookPage;
import handbook.client.book.gui.GuiBookEntry;
import handbook.client.book.template.TemplateComponent;

public class ComponentTooltip extends TemplateComponent {

    @SerializedName("tooltip") public IVariable[] tooltipRaw;

    int width, height;

    transient List<Component> tooltip;

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
        super.onVariablesAvailable(lookup, registries);
        for (int i = 0; i < tooltipRaw.length; i++) {
            tooltipRaw[i] = lookup.apply(tooltipRaw[i]);
        }
    }

    @Override
    public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
        tooltip = new ArrayList<>();

        for (IVariable s : tooltipRaw) {
            tooltip.add(s.as(Component.class));
        }
    }

    @Override
    public void render(GuiGraphics graphics, BookPage page, int mouseX, int mouseY, float pticks) {
        if (page.parent.isMouseInRelativeRange(mouseX, mouseY, x, y, width, height)) {
            page.parent.setTooltip(tooltip);
        }
    }

}
