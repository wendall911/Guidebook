package guidebook.client.book.template.component;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import guidebook.api.IVariable;
import guidebook.api.GuidebookAPI;
import guidebook.client.base.ClientTicker;
import guidebook.client.book.BookContentsBuilder;
import guidebook.client.book.BookEntry;
import guidebook.client.book.BookPage;
import guidebook.client.book.gui.GuiBookEntry;
import guidebook.client.book.page.PageEntity;
import guidebook.client.book.template.TemplateComponent;
import guidebook.common.util.EntityUtil;

public class ComponentEntity extends TemplateComponent {

    @SerializedName("entity") public IVariable entityId;

    @SerializedName("render_size") float renderSize = 100;

    boolean rotate = true;
    @SerializedName("default_rotation") float defaultRotation = -45f;

    transient boolean errored;
    transient Entity entity;
    transient Function<Level, Entity> creator;
    transient float renderScale, offset;

    @Override
    public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
        creator = EntityUtil.loadEntity(entityId.asString());
    }

    @Override
    public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
        loadEntity(page.mc.level);
    }

    @Override
    public void render(GuiGraphics graphics, BookPage page, int mouseX, int mouseY, float pticks) {
        if (errored) {
            graphics.drawString(page.fontRenderer, Component.translatable("guidebook.gui.lexicon.loading_error"), x, y, 0xFF0000, false);
        }

        if (entity != null) {
            float rotation = rotate ? ClientTicker.total : defaultRotation;
            PageEntity.renderEntity(graphics, entity, x, y, rotation, renderScale, offset);
        }
    }

    @Override
    public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
        super.onVariablesAvailable(lookup, registries);
        entityId = lookup.apply(entityId);
    }

    private void loadEntity(Level world) {
        if (!errored && (entity == null || !entity.isAlive() || entity.level() != world)) {
            try {
                entity = creator.apply(world);
                float width = entity.getBbWidth();
                float height = entity.getBbHeight();

                float entitySize = Math.max(width, height);
                entitySize = Math.max(1F, entitySize);

                renderScale = renderSize / entitySize * 0.8F;
                offset = Math.max(height, entitySize) * 0.5F;
            }
            catch (Exception e) {
                errored = true;
                GuidebookAPI.LOGGER.error("Failed to load entity", e);
            }
        }
    }

}
