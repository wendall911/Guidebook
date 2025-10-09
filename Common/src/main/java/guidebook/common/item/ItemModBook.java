package guidebook.common.item;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import guidebook.api.GuidebookAPI;
import guidebook.client.book.BookEntry;
import guidebook.common.base.GuidebookSounds;
import guidebook.common.book.Book;
import guidebook.common.book.BookRegistry;

public class ItemModBook extends Item {

    public ItemModBook() {
        super(new Item.Properties().stacksTo(1));
    }

    public static float getCompletion(ItemStack stack) {
        Book book = getBook(stack);
        float progression = 0F; // default incomplete

        if (book != null) {
            int totalEntries = 0;
            int unlockedEntries = 0;

            for (BookEntry entry : book.getContents().entries.values()) {
                if (!entry.isSecret()) {
                    totalEntries++;
                    if (!entry.isLocked()) {
                        unlockedEntries++;
                    }
                }
            }

            progression = ((float) unlockedEntries) / Math.max(1f, (float) totalEntries);
        }

        return progression;
    }

    public static ItemStack forBook(Book book) {
        return forBook(book.id);
    }

    public static ItemStack forBook(ResourceLocation book) {
        ItemStack stack = new ItemStack(GuidebookItems.BOOK);

        stack.set(GuidebookDataComponents.BOOK, book);

        return stack;
    }

    // SoftImplement IForgeItem
    public String getCreatorModId(ItemStack stack) {
        var book = getBook(stack);
        if (book != null) {
            return book.owner.getId();
        }
        return BuiltInRegistries.ITEM.getKey(this).getNamespace();
    }

    public static Book getBook(ItemStack stack) {
        ResourceLocation res = getBookId(stack);
        if (res == null) {
            return null;
        }
        return BookRegistry.INSTANCE.books.get(res);
    }

    private static ResourceLocation getBookId(ItemStack stack) {
        if (!stack.has(GuidebookDataComponents.BOOK)) {
            return null;
        }

        return stack.get(GuidebookDataComponents.BOOK);
    }

    @Override
    public Component getName(ItemStack stack) {
        Book book = getBook(stack);
        if (book != null) {
            return Component.translatable(book.name);
        }

        return super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);

        ResourceLocation rl = getBookId(stack);
        if (flagIn.isAdvanced()) {
            tooltip.add(Component.literal("Book ID: " + rl).withStyle(ChatFormatting.GRAY));
        }

        Book book = getBook(stack);
        if (book != null && !book.getContents().isErrored()) {
            tooltip.add(book.getSubtitle().withStyle(ChatFormatting.GRAY));
        }
        else if (book == null) {
            if (rl == null) {
                tooltip.add(Component.translatable("item.guidebook.guide_book.undefined")
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
            else {
                tooltip.add(Component.translatable("item.guidebook.guide_book.invalid", rl)
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        Book book = getBook(stack);
        if (book == null) {
            return new InteractionResultHolder<>(InteractionResult.FAIL, stack);
        }

        if (playerIn instanceof ServerPlayer) {
            GuidebookAPI.get().openBookGUI((ServerPlayer) playerIn, book.id);

            // This plays the sound to others nearby, playing to the actual opening player handled from the packet
            SoundEvent sfx = GuidebookSounds.getSound(book.openSound, GuidebookSounds.BOOK_OPEN);
            playerIn.playSound(sfx, 1F, (float) (0.7 + Math.random() * 0.4));
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

}
