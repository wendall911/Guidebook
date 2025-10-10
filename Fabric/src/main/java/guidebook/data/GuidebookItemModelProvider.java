package guidebook.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;

import guidebook.common.item.GuidebookItems;


public class GuidebookItemModelProvider extends FabricModelProvider {

    public GuidebookItemModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        addBookModel(itemModelGenerator, GuidebookItems.BOOK_ID, GuidebookItems.BOOK_BROWN_ID);
        addBookModel(itemModelGenerator, GuidebookItems.BOOK_BLUE_ID, GuidebookItems.BOOK_BLUE_ID);
        addBookModel(itemModelGenerator, GuidebookItems.BOOK_BROWN_ID, GuidebookItems.BOOK_BROWN_ID);
        addBookModel(itemModelGenerator, GuidebookItems.BOOK_CYAN_ID, GuidebookItems.BOOK_CYAN_ID);
        addBookModel(itemModelGenerator, GuidebookItems.BOOK_GRAY_ID, GuidebookItems.BOOK_GRAY_ID);
        addBookModel(itemModelGenerator, GuidebookItems.BOOK_GREEN_ID, GuidebookItems.BOOK_GREEN_ID);
        addBookModel(itemModelGenerator, GuidebookItems.BOOK_PURPLE_ID, GuidebookItems.BOOK_PURPLE_ID);
        addBookModel(itemModelGenerator, GuidebookItems.BOOK_RED_ID, GuidebookItems.BOOK_RED_ID);
        //ModelTemplates.FLAT_ITEM.create(GuidebookItems.BOOK_ID, TextureMapping.layer0(GuidebookItems.BOOK_BROWN_ID), itemModelGenerator.output);
        //itemModelGenerator.generateFlatItem(GuidebookItems.BOOK, FLAT_HANDHELD_ITEM);
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
