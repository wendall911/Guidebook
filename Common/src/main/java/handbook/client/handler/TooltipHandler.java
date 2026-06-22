package handbook.client.handler;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.datafixers.util.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Util;

import handbook.client.base.ClientTicker;
import handbook.client.book.BookEntry;
import handbook.client.book.ClientBookRegistry;
import handbook.client.book.gui.GuiBook;
import handbook.common.book.Book;
import handbook.common.util.ColorHelper;
import handbook.common.util.ColorHelper.HandbookColors;
import handbook.common.util.ItemStackUtil;
import handbook.config.HandbookConfig;

public class TooltipHandler {

    private static float lexiconLookupTime = 0;

    public static void onTooltip(GuiGraphicsExtractor guiGraphics, ItemStack stack, int mouseX, int mouseY) {
        Minecraft mc = Minecraft.getInstance();
        int tooltipX = mouseX;
        int tooltipY = mouseY - 4;

        if (mc.player != null && !(mc.gui.screen() instanceof GuiBook)) {
            int lexSlot = -1;
            ItemStack lexiconStack = ItemStack.EMPTY;
            Pair<BookEntry, Integer> lexiconEntry = null;

            for (int i = 0; i < Inventory.getSelectionSize(); i++) {
                ItemStack stackAt = mc.player.getInventory().getItem(i);
                if (!stackAt.isEmpty()) {
                    Book book = ItemStackUtil.getBookFromStack(stackAt);

                    if (book != null) {
                        Pair<BookEntry, Integer> entry = book.getContents().getEntryForStack(stack);

                        if (entry != null && !entry.getFirst().isLocked()) {
                            lexiconStack = stackAt;
                            lexSlot = i;
                            lexiconEntry = entry;
                            break;
                        }
                    }
                }
            }

            if (lexSlot > -1) {
                int x = tooltipX - 34;

                guiGraphics.fill(x - 4, tooltipY - 4, x + 20,
                    tooltipY + 26, ColorHelper.fillBlack(0.17F));
                guiGraphics.fill(x - 6, tooltipY - 6, x + 22,
                    tooltipY + 28, ColorHelper.fillBlack(0.17F));

                if (HandbookConfig.Client.useShiftForQuickLookup() ? mc.hasShiftDown() : mc.hasControlDown()) {
                    lexiconLookupTime += ClientTicker.delta;

                    int cx = x + 8;
                    int cy = tooltipY + 8;
                    float r = 12;
                    float requiredTime = HandbookConfig.Client.quickLookupTime();
                    float angles = lexiconLookupTime / requiredTime * 360F;
                    ByteBufferBuilder buffer = new ByteBufferBuilder(786432);
                    BufferBuilder buf = new BufferBuilder(buffer, PrimitiveTopology.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

                    float a = 0.5F + 0.2F * ((float) Math.cos(ClientTicker.total / 10) * 0.5F + 0.5F);
                    buf.addVertex(cx, cy, 0).setColor(0F, 0.5F, 0F, a);

                    for (float i = angles; i > 0; i--) {
                        double rad = (i - 90) / 180F * Math.PI;
                        buf.addVertex((float) (cx + Math.cos(rad) * r),
                            (float) (cy + Math.sin(rad) * r), 0).setColor(0F, 1F, 0F, 1F);
                    }

                    buf.addVertex(cx, cy, 0).setColor(0F, 1F, 0F, 0F);

                    if (lexiconLookupTime >= requiredTime) {
                        int spread = lexiconEntry.getSecond();

                        mc.player.getInventory().setSelectedSlot(lexSlot);

                        ClientBookRegistry.INSTANCE.displayBookGui(
                            lexiconEntry.getFirst().getBook().id, lexiconEntry.getFirst().getId(), spread * 2);
                    }
                }
                else {
                    lexiconLookupTime = 0F;
                }

                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(0, 0);
                guiGraphics.item(lexiconStack, x, tooltipY);
                guiGraphics.itemDecorations(mc.font, lexiconStack, x, tooltipY);
                guiGraphics.pose().popMatrix();

                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(0, 0);
                guiGraphics.text(mc.font, "?", x + 10, tooltipY + 8, HandbookColors.WHITE.toColor(), true);

                guiGraphics.pose().scale(0.5F, 0.5F);

                boolean mac = Util.getPlatform() == Util.OS.OSX;
                Component key = Component.literal(HandbookConfig.Client.useShiftForQuickLookup() ? "Shift" : mac ? "Cmd" : "Ctrl")
                    .withStyle(ChatFormatting.BOLD);

                guiGraphics.text(mc.font, key, (x + 10) * 2 - 16, (tooltipY + 8) * 2 + 20, HandbookColors.WHITE.toColor(), true);
                guiGraphics.pose().popMatrix();
            }
            else {
                lexiconLookupTime = 0F;
            }
        }
        else {
            lexiconLookupTime = 0F;
        }
    }

}
