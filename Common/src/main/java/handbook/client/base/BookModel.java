package handbook.client.base;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import handbook.common.book.Book;
import handbook.common.item.HandbookBook;

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
        Book book = HandbookBook.getBook(stack);

        if (book != null) {
            // TODO See if this is needed at all
            //itemModel = new CuboidItemModelWrapper.Unbaked(book.model, Optional.empty(), List.of()).bake(bakingContext, level.);
        }

        itemModel.update(renderState, stack, itemModelResolver, displayContext, level, owner, seed);
    }

}
