package guidebook.client.base;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import guidebook.common.book.Book;
import guidebook.common.item.ItemModBook;

public class BookModel implements ItemModel {

    private final ItemModel itemModel;
    private final BakingContext bakingContext;

    public BookModel(ItemModel bookModel, BakingContext bakingContext) {
        this.itemModel = bookModel;
        this.bakingContext = bakingContext;
    }

    @Override
    public void update(@NotNull ItemStackRenderState renderState,
            @NotNull ItemStack stack,
            @NotNull ItemModelResolver itemModelResolver,
            @NotNull ItemDisplayContext displayContext,
            @Nullable ClientLevel level,
            @Nullable ItemOwner owner,
            int seed) {
        ItemModel itemModel = this.itemModel;
        Book book = ItemModBook.getBook(stack);

        if (book != null) {
            itemModel = new BlockModelWrapper.Unbaked(book.model, List.of()).bake(bakingContext);
        }

        itemModel.update(renderState, stack, itemModelResolver, displayContext, level, owner, seed);
    }

}
