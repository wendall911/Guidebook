package handbook.data;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import handbook.common.item.HandbookItems;

public class HandbookItemTagsProvider extends IntrinsicHolderTagsProvider<Item> {

    public HandbookItemTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider) {
        super(output, Registries.ITEM, lookupProvider, (item) -> item.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(@NotNull Provider provider) {
        this.tag(ItemTags.BOOKSHELF_BOOKS).add(HandbookItems.BOOK);
        this.tag(ItemTags.LECTERN_BOOKS).add(HandbookItems.BOOK);
    }

}
