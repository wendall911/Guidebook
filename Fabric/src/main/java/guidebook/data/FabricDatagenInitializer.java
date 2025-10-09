package guidebook.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import guidebook.api.GuidebookAPI;

public class FabricDatagenInitializer implements DataGeneratorEntrypoint {

    private static FabricTagProvider.BlockTagProvider fabricBlockTagProvider;

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        fabricBlockTagProvider = pack.addProvider(FabricBlockTagProvider::new);

        if (System.getProperty(GuidebookAPI.MODID + ".common_datagen") != null) {
            configureCommonDatagen(pack);
        }
    }

    public static void configureCommonDatagen(FabricDataGenerator.Pack pack) {
        //pack.addProvider((dataOutput, registryFuture) -> new GuidebookItemTagProvider(dataOutput, registryFuture, fabricBlockTagProvider.contentsGetter()));
        //fabricDataGenerator.addProvider(GuidebookItemModelProvider::new);
        //fabricDataGenerator.addProvider(GuidebookRecipeProvider::new);
        //fabricDataGenerator.addProvider(GuidebookAdvancementProvider::new);
        //fabricDataGenerator.addProvider(GuidebookLanguageProvider::new);
    }

}
