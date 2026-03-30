package handbook.client.book.page;

import java.util.function.Function;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import org.joml.Matrix3x2fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import handbook.api.HandbookAPI;
import handbook.client.base.ClientTicker;
import handbook.client.book.BookContentsBuilder;
import handbook.client.book.BookEntry;
import handbook.client.book.gui.GuiBook;
import handbook.client.book.gui.GuiBookEntry;
import handbook.client.book.page.abstr.PageWithText;
import handbook.common.util.ColorHelper.HandbookColors;
import handbook.common.util.EntityUtil;

public class PageEntity extends PageWithText {

    @SerializedName("entity") public String entityId;

    float scale = 1F;
    @SerializedName("offset") float extraOffset = 0F;
    String name;

    boolean rotate = true;
    @SerializedName("default_rotation") float defaultRotation = -45f;

    transient boolean errored;
    transient Entity entity;
    transient Function<Level, Entity> creator;
    transient float renderScale, offset;

    @Override
    public void build(Level level, BookEntry entry, BookContentsBuilder builder, int pageNum) {
        super.build(level, entry, builder, pageNum);

        creator = EntityUtil.loadEntity(entityId);
    }

    @Override
    public void onDisplayed(GuiBookEntry parent, int left, int top) {
        super.onDisplayed(parent, left, top);
        Minecraft mc = parent.getMinecraft();

        if (mc != null) {
            loadEntity(mc.level);
        }
    }

    @Override
    public int getTextHeight() {
        return 115;
    }

    @Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        int x = GuiBook.PAGE_WIDTH / 2 - 53;
        int y = 7;

        GuiBook.drawFromTexture(guiGraphics, book, x, y, 405, 149, 106, 106);

        if (name == null || name.isEmpty()) {
            if (entity != null) {
                parent.drawCenteredStringNoShadow(guiGraphics, entity.getName().getVisualOrderText(),
                    GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
            }
        } else {
            parent.drawCenteredStringNoShadow(guiGraphics, name, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);
        }

        if (errored) {
            guiGraphics.text(fontRenderer, I18n.get("handbook.gui.lexicon.loading_error"),
                18, 60, HandbookColors.ERROR_RED.toColor(), true);
        }

        if (entity != null) {
            float rotation = rotate ? ClientTicker.total : defaultRotation;

            renderEntity(guiGraphics, entity, 58, 60, rotation, renderScale, offset, mouseX, mouseY);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }

    /*
     * Render an entity at the given position with the given scale and rotation.
     * TODO: Figure out why this isn't working
     */
    public static void renderEntity(GuiGraphicsExtractor guiGraphics, Entity entity, float x, float y, float rotation,
                                    float renderScale, float offset, int mouseX, int mouseY) {
        Matrix3x2fStack poseStack = guiGraphics.pose();
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super Entity, ?> renderer = entityRenderDispatcher.getRenderer(entity);
        EntityRenderState renderState = renderer.createRenderState(entity, 1.0F);
        Vector3f translation = new Vector3f(0F, (28F / renderScale / 2F), 0F);
        Quaternionf rotationQuat = new Quaternionf().rotateYXZ((float) Math.toRadians(180 + rotation), 0F, 0F);

        //TODO remove this when entity rendering is implemented
        guiGraphics.text(fontRenderer, "Not Implemented!", 18, 60, HandbookColors.ERROR_RED.toColor(), true);

        guiGraphics.enableScissor((int) x, (int) y, (int) x + 106, (int) y + 106);

        renderState.lightCoords = 0xF000F0; // Full brightness
        //TODO is this needed?
        //renderState.hitboxesRenderState = null; // Disable hitboxes
        renderState.shadowPieces.clear();
        renderState.outlineColor = 0;

        poseStack.pushMatrix();
        poseStack.translate(x, y);
        poseStack.scale(renderScale, renderScale);
        poseStack.translate(0, offset);

        guiGraphics.entity(renderState, renderScale, translation, rotationQuat,
            null, 0, 0, 0, 1);

        poseStack.popMatrix();

        guiGraphics.disableScissor();
    }

    private void loadEntity(Level level) {
        if (!errored && (entity == null || !entity.isAlive() || entity.level() != level)) {
            try {
                entity = creator.apply(level);

                if (entity == null) {
                    errored = true;
                    HandbookAPI.LOGGER.error("Failed to load entity: {}", entityId);
                    return;
                }

                float width = entity.getBbWidth();
                float height = entity.getBbHeight();

                float entitySize = Math.max(1F, Math.max(width, height));

                renderScale = 100F / entitySize * 0.8F * scale;
                offset = Math.max(height, entitySize) * 0.5F + extraOffset;
            }
            catch (Exception e) {
                errored = true;

                HandbookAPI.LOGGER.error("Failed to load entity", e);
            }
        }
    }

}
