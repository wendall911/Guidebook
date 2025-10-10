package guidebook.data;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;

import guidebook.common.item.GuidebookItems;

public class GuidebookItemTagsProvider extends ItemTagsProvider {

    public GuidebookItemTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags);
    }

    @Override
    protected void addTags(@NotNull Provider provider) {
        this.tag(ItemTags.BOOKSHELF_BOOKS).add(GuidebookItems.BOOK);
        this.tag(ItemTags.LECTERN_BOOKS).add(GuidebookItems.BOOK);
    }

}
