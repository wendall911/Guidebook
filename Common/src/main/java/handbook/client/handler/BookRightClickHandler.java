package handbook.client.handler;

import org.jspecify.annotations.Nullable;

import com.mojang.blaze3d.platform.Window;
import com.mojang.datafixers.util.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import handbook.client.book.BookEntry;
import handbook.common.book.Book;
import handbook.common.util.ColorHelper.HandbookColors;
import handbook.common.util.ItemStackUtil;

public class BookRightClickHandler {

    public static void onRenderHUD(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player != null && mc.screen == null) {
            ItemStack bookStack = player.getMainHandItem();
            Book book = ItemStackUtil.getBookFromStack(bookStack);

            if (book != null) {
                Pair<BookEntry, Integer> hover = getHoveredEntry(book);

                if (hover != null) {
                    BookEntry entry = hover.getFirst();
                    if (!entry.isLocked()) {
                        Window window = mc.getWindow();
                        int x = window.getGuiScaledWidth() / 2 + 3;
                        int y = window.getGuiScaledHeight() / 2 + 3;
                        Component s = Component.translatable("handbook.gui.lexicon." + (player.isShiftKeyDown() ? "view" : "sneak"))
                            .withStyle(ChatFormatting.ITALIC);

                        entry.getIcon().render(guiGraphics, x, y);

                        guiGraphics.pose().pushMatrix();
                        guiGraphics.pose().translate(0, 0);
                        guiGraphics.pose().scale(0.5F, 0.5F);
                        guiGraphics.item(bookStack, (x + 8) * 2, (y + 8) * 2);
                        guiGraphics.itemDecorations(mc.font, bookStack, (x + 8) * 2, (y + 8) * 2);
                        guiGraphics.pose().popMatrix();

                        guiGraphics.text(mc.font, entry.getName(), x + 18, y + 3, HandbookColors.WHITE.toColor(), false);

                        guiGraphics.pose().pushMatrix();
                        guiGraphics.pose().scale(0.75F, 0.75F);

                        guiGraphics.text(mc.font, s, (int) ((x + 18) / 0.75F), (int) ((y + 14) / 0.75F), HandbookColors.GRAY.toColor(), false);
                        guiGraphics.pose().popMatrix();
                    }
                }
            }
        }
    }

    public static InteractionResult onRightClick(Player player, Level world, InteractionHand hand, BlockHitResult hit) {
        ItemStack bookStack = player.getMainHandItem();

        if (world.isClientSide() && player.isShiftKeyDown()) {
            Book book = ItemStackUtil.getBookFromStack(bookStack);

            if (book != null) {
                Pair<BookEntry, Integer> hover = getHoveredEntry(book);
                if (hover != null) {
                    int page = hover.getSecond() * 2;
                    book.getContents().setTopEntry(hover.getFirst().getId(), page);
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Nullable
    private static Pair<BookEntry, Integer> getHoveredEntry(Book book) {
        Minecraft mc = Minecraft.getInstance();
        HitResult res = mc.hitResult;
        if (mc.level != null && res instanceof BlockHitResult hit) {
            BlockPos pos = hit.getBlockPos();
            BlockState state = mc.level.getBlockState(pos);
            Block block = state.getBlock();
            ItemStack picked = new ItemStack(block.asItem());

            if (!picked.isEmpty()) {
                return book.getContents().getEntryForStack(picked);
            }
        }

        return null;
    }

}
