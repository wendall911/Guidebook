package handbook.data;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

import handbook.common.item.HandbookItems;

public class HandbookItemModelProvider extends FabricModelProvider {

    public static final List<Pair<Identifier, Item>> ALL_MODELS = List.of(
        Pair.of(getModelId(HandbookItems.BOOK_BROWN_ID), HandbookItems.BOOK_BROWN),
        Pair.of(getModelId(HandbookItems.BOOK_BLUE_ID), HandbookItems.BOOK_BLUE),
        Pair.of(getModelId(HandbookItems.BOOK_CYAN_ID), HandbookItems.BOOK_CYAN),
        Pair.of(getModelId(HandbookItems.BOOK_GRAY_ID), HandbookItems.BOOK_GRAY),
        Pair.of(getModelId(HandbookItems.BOOK_GREEN_ID), HandbookItems.BOOK_GREEN),
        Pair.of(getModelId(HandbookItems.BOOK_PURPLE_ID), HandbookItems.BOOK_PURPLE),
        Pair.of(getModelId(HandbookItems.BOOK_RED_ID), HandbookItems.BOOK_RED)
    );

    public HandbookItemModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        // Default book model (brown)
        Identifier defaultModel = getModelId(HandbookItems.BOOK_ID);
        Identifier bookTemplate = ModelTemplates.FLAT_ITEM.create(
            defaultModel,
            TextureMapping.layer0(HandbookItems.BOOK_BROWN),
            itemModelGenerator.modelOutput
        );
        itemModelGenerator.itemModelOutput.accept(HandbookItems.BOOK, ItemModelUtils.plainModel(bookTemplate));

        /*
         * Other color variants
         * These all use the same model, just different textures.
         * Generate so other mods can use them if they want.
         */
        ALL_MODELS.stream()
            .filter(id -> !id.getFirst().equals(defaultModel)) // skip default, already done above
            .forEach(id -> {
                ModelTemplates.FLAT_ITEM.create(
                    id.getFirst(),
                    TextureMapping.layer0(id.getSecond()),
                    itemModelGenerator.modelOutput
                );
            });
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        // NO-OP
    }

    public static Identifier getModelId(Identifier variant) {
        return variant.withPrefix("item/");
    }

}
