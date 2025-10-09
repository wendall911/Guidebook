package guidebook.client.book.template.component;

import java.util.function.UnaryOperator;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

import guidebook.api.IVariable;
import guidebook.client.book.BookContentsBuilder;
import guidebook.client.book.BookEntry;
import guidebook.client.book.BookPage;
import guidebook.client.book.template.TemplateComponent;

public class ComponentImage extends TemplateComponent {

    public String image;

    public int u, v, width, height;

    @SerializedName("texture_width") public int textureWidth = 256;
    @SerializedName("texture_height") public int textureHeight = 256;

    public float scale = 1F;

    transient ResourceLocation resource;

    @Override
    public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
        resource = ResourceLocation.tryParse(image);
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
        super.onVariablesAvailable(lookup, registries);
        image = lookup.apply(IVariable.wrap(image, registries)).asString();
    }

    @Override
    public void render(GuiGraphics graphics, BookPage page, int mouseX, int mouseY, float pticks) {
        if (scale == 0F) {
            return;
        }

        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        graphics.pose().scale(scale, scale, scale);
        graphics.setColor(1F, 1F, 1F, 1F);
        RenderSystem.enableBlend();
        graphics.blit(resource, 0, 0, u, v, width, height, textureWidth, textureHeight);
        graphics.pose().popPose();
    }

}
