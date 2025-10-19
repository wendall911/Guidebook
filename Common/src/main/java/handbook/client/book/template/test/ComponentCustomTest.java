package handbook.client.book.template.test;

import java.util.function.UnaryOperator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;

import handbook.api.IComponentRenderContext;
import handbook.api.ICustomComponent;
import handbook.api.IVariable;
import handbook.api.HandbookAPI;

public class ComponentCustomTest implements ICustomComponent {
    private transient int x, y;
    private transient String text = "";

    @Override
    public void build(int componentX, int componentY, int pageNum) {
        x = componentX;
        y = componentY;
        HandbookAPI.LOGGER.debug("Custom Component Test built at ({}, {}) page {}", componentX, componentY, pageNum);
    }

    @Override
    public void render(GuiGraphics graphics, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
        Component toRender = Component.literal(text).setStyle(context.getFont());

        graphics.drawString(Minecraft.getInstance().font, toRender, x, y, -1, true);
    }

    @Override
    public boolean mouseClicked(IComponentRenderContext context, double mouseX, double mouseY, int mouseButton) {
        HandbookAPI.LOGGER.debug("Custom Component Test clicked at ({}, {}) button {}", mouseX, mouseY, mouseButton);

        return false;
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
        text = lookup.apply(IVariable.wrap("First we eat #spaghet#, then we drink #pop#", registries)).asString();
    }

}
