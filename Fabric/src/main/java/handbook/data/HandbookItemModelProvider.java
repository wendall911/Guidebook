package handbook.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;

import handbook.common.item.HandbookItems;


public class HandbookItemModelProvider extends FabricModelProvider {

    public HandbookItemModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        addBookModel(itemModelGenerator, HandbookItems.BOOK_ID, HandbookItems.BOOK_BROWN_ID);
        addBookModel(itemModelGenerator, HandbookItems.BOOK_BLUE_ID, HandbookItems.BOOK_BLUE_ID);
        addBookModel(itemModelGenerator, HandbookItems.BOOK_BROWN_ID, HandbookItems.BOOK_BROWN_ID);
        addBookModel(itemModelGenerator, HandbookItems.BOOK_CYAN_ID, HandbookItems.BOOK_CYAN_ID);
        addBookModel(itemModelGenerator, HandbookItems.BOOK_GRAY_ID, HandbookItems.BOOK_GRAY_ID);
        addBookModel(itemModelGenerator, HandbookItems.BOOK_GREEN_ID, HandbookItems.BOOK_GREEN_ID);
        addBookModel(itemModelGenerator, HandbookItems.BOOK_PURPLE_ID, HandbookItems.BOOK_PURPLE_ID);
        addBookModel(itemModelGenerator, HandbookItems.BOOK_RED_ID, HandbookItems.BOOK_RED_ID);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        // NO-OP
    }

    public void addBookModel(ItemModelGenerators itemModelGenerator, ResourceLocation book, ResourceLocation bookTexture) {
        ResourceLocation bookModel = ResourceLocation.fromNamespaceAndPath(book.getNamespace(), "item/" + book.getPath());
        ResourceLocation bookTextureModel = ResourceLocation.fromNamespaceAndPath(bookTexture.getNamespace(), "item/" + bookTexture.getPath());

        ModelTemplates.FLAT_ITEM.create(bookModel, TextureMapping.layer0(bookTextureModel), itemModelGenerator.output);
    }

}
