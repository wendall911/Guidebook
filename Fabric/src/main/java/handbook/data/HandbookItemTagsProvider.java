package handbook.data;

import java.util.concurrent.CompletableFuture;

import org.jspecify.annotations.NonNull;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import handbook.common.item.HandbookItems;

public class HandbookItemTagsProvider extends FabricTagsProvider<Item> {

    public HandbookItemTagsProvider(FabricPackOutput output, CompletableFuture<Provider> lookupProvider) {
        super(output, Registries.ITEM, lookupProvider);
    }

    @Override
    protected void addTags(@NonNull Provider provider) {
        ResourceKey<Item> bookKey = BuiltInRegistries.ITEM.getResourceKey(HandbookItems.BOOK).orElseThrow();

        this.tag(ItemTags.BOOKSHELF_BOOKS).add(bookKey);
        this.tag(ItemTags.LECTERN_BOOKS).add(bookKey);
    }

}
