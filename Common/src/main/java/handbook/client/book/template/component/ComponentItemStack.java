package handbook.client.book.template.component;

import java.util.function.UnaryOperator;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

import handbook.api.IVariable;
import handbook.client.book.BookContentsBuilder;
import handbook.client.book.BookEntry;
import handbook.client.book.BookPage;
import handbook.client.book.template.TemplateComponent;

public class ComponentItemStack extends TemplateComponent {

    public IVariable item;

    private boolean framed;
    @SerializedName("link_recipe") private boolean linkedRecipe;

    private transient ItemStack[] items;

    @Override
    public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
        if (linkedRecipe) {
            for (ItemStack stack : items) {
                entry.addRelevantStack(builder, stack, pageNum);
            }
        }
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
        super.onVariablesAvailable(lookup, registries);
        items = lookup.apply(item).as(ItemStack[].class);
    }

    @Override
    public void render(GuiGraphics guiGraphics, BookPage page, int mouseX, int mouseY, float partialTicks) {
        if (items.length == 0) {
            return;
        }

        if (framed) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, page.book.craftingTexture,
                x - 5, y - 5, 20, 102, 26, 26, 128, 256);
        }

        page.parent.renderItemStack(guiGraphics, x, y, mouseX, mouseY,
            items[(page.parent.ticksInBook / 20) % items.length]);
    }

}
