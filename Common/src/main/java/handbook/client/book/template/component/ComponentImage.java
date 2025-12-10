package handbook.client.book.template.component;

import java.util.function.UnaryOperator;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

import handbook.api.IVariable;
import handbook.client.book.BookContentsBuilder;
import handbook.client.book.BookEntry;
import handbook.client.book.BookPage;
import handbook.client.book.template.TemplateComponent;

public class ComponentImage extends TemplateComponent {

    public String image;

    public int u, v, width, height;

    @SerializedName("texture_width") public int textureWidth = 256;
    @SerializedName("texture_height") public int textureHeight = 256;

    public float scale = 1F;

    transient Identifier resource;

    @Override
    public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
        resource = Identifier.tryParse(image);
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
        super.onVariablesAvailable(lookup, registries);
        image = lookup.apply(IVariable.wrap(image, registries)).asString();
    }

    @Override
    public void render(GuiGraphics guiGraphics, BookPage page, int mouseX, int mouseY, float pticks) {
        if (scale == 0F) {
            return;
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(x, y);
        guiGraphics.pose().scale(scale, scale);
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, resource,
            0, 0, u, v, width, height, textureWidth, textureHeight);
        guiGraphics.pose().popMatrix();
    }

}
