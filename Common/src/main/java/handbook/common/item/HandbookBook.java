package handbook.common.item;

import java.util.function.Consumer;

import org.jspecify.annotations.NonNull;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import technology.roughness.whitenoise.platform.Services;
import technology.roughness.whitenoise.util.ResourceLocationHelper;

import handbook.api.HandbookAPI;
import handbook.common.base.HandbookSounds;
import handbook.common.book.Book;
import handbook.common.book.BookRegistry;

import static handbook.data.HandbookInternalBookProvider.INTRO_BOOK_TRANSLATION_KEY;

public class HandbookBook extends Item {

    public HandbookBook(Properties properties) {
        super(properties);
    }

    public static ItemStack forBook(Book book) {
        return forBook(book.id);
    }

    public static ItemStack forBook(Identifier book) {
        ItemStack stack = new ItemStack(HandbookItems.BOOK);

        stack.set(HandbookDataComponents.BOOK, book);

        return stack;
    }

    public static Book getBook(ItemStack stack) {
        Identifier res = getBookId(stack);

        if (res == null) {
            return null;
        }

        return BookRegistry.INSTANCE.books.get(res);
    }

    /*
     * Gets the book ID from the stack, either from Component or by looking up the item
     */
    private static Identifier getBookId(ItemStack stack) {
        if (stack.has(HandbookDataComponents.BOOK)) {
            return stack.get(HandbookDataComponents.BOOK);
        }
        else {
            Book book = BookRegistry.INSTANCE.books.getOrDefault(ResourceLocationHelper.getItemStackId(stack), null);

            if (book != null) {
                return book.id;
            }
        }

        return null;
    }

    @Override
    public @NonNull Component getName(@NonNull ItemStack stack) {
        Book book = getBook(stack);

        if (book != null) {
            return Component.translatable(book.name);
        }

        return super.getName(stack);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(@NonNull ItemStack stack, @NonNull TooltipContext context,
            @NonNull TooltipDisplay tooltipDisplay, @NonNull Consumer<Component> tooltip, @NonNull TooltipFlag flagIn) {

        if (!Services.WN_PLATFORM.isPhysicalClient()) {
            return;
        }

        Identifier rl = getBookId(stack);

        if (flagIn.isAdvanced()) {
            tooltip.accept(Component.literal("Book ID: " + rl).withStyle(ChatFormatting.GRAY));
        }

        Book book = getBook(stack);

        if (book != null && !book.getContents().isErrored()) {
            tooltip.accept(book.getSubtitle().withStyle(ChatFormatting.GRAY));
        }
        else if (book == null) {
            if (rl == null) {
                tooltip.accept(Component.translatable(INTRO_BOOK_TRANSLATION_KEY + ".undefined")
                    .withStyle(ChatFormatting.DARK_GRAY));
            }
            else {
                tooltip.accept(Component.translatable(INTRO_BOOK_TRANSLATION_KEY + ".invalid", rl)
                    .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, Player playerIn, @NonNull InteractionHand hand) {
        ItemStack stack = playerIn.getItemInHand(hand);
        Book book = getBook(stack);

        if (book == null) {
            return InteractionResult.FAIL;
        }

        if (playerIn instanceof ServerPlayer) {
            // This plays the sound to others nearby, playing to the actual opening player handled from the packet
            SoundEvent sfx = HandbookSounds.getSound(book.openSound, HandbookSounds.BOOK_OPEN);
            playerIn.playSound(sfx, 1F, (float) (0.7 + Math.random() * 0.4));

            HandbookAPI.get().openBookGUI((ServerPlayer) playerIn, book.id);
        }

        return InteractionResult.SUCCESS;
    }

}
