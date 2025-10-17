package guidebook.data;

import java.util.List;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;

import guidebook.common.item.GuidebookItems;

public class GuidebookItemModelProvider extends FabricModelProvider {

    public static final List<ResourceLocation> ALL_MODELS = List.of(
        getModelId(GuidebookItems.BOOK_BROWN_ID),
        getModelId(GuidebookItems.BOOK_BLUE_ID),
        getModelId(GuidebookItems.BOOK_CYAN_ID),
        getModelId(GuidebookItems.BOOK_GRAY_ID),
        getModelId(GuidebookItems.BOOK_GREEN_ID),
        getModelId(GuidebookItems.BOOK_PURPLE_ID),
        getModelId(GuidebookItems.BOOK_RED_ID)
    );

    public GuidebookItemModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        // Default book model (brown)
        ResourceLocation defaultModel = getModelId(GuidebookItems.BOOK_ID);
        ResourceLocation bookTemplate = ModelTemplates.FLAT_ITEM.create(
            defaultModel,
            TextureMapping.layer0(GuidebookItems.BOOK_BROWN_ID.withPrefix("item/")),
            itemModelGenerator.modelOutput
        );
        itemModelGenerator.itemModelOutput.accept(GuidebookItems.BOOK, ItemModelUtils.plainModel(bookTemplate));

        /*
         * Other color variants
         * These all use the same model, just different textures.
         * Generate so other mods can use them if they want.
         */
        ALL_MODELS.stream()
            .filter(id -> !id.equals(defaultModel)) // skip default, already done above
            .forEach(id -> {
                ModelTemplates.FLAT_ITEM.create(
                    id,
                    TextureMapping.layer0(id),
                    itemModelGenerator.modelOutput
                );
            });
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        // NO-OP
    }

    public static ResourceLocation getModelId(ResourceLocation variant) {
        return variant.withPrefix("item/");
    }

}
