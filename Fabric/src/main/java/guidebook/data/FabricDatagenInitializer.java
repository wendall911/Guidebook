package guidebook.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

import guidebook.api.GuidebookAPI;
import guidebook.data.test.GuidebookTestLanguageProvider;

public class FabricDatagenInitializer implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        if (System.getProperty(GuidebookAPI.MODID + ".common_datagen") != null) {
            configureCommonDatagen(pack);
        }
        else if (System.getProperty(GuidebookAPI.MODID + ".fabric_test_datagen") != null) {
            configureFabicTestDatagen(pack);
        }
        else if (System.getProperty(GuidebookAPI.MODID + ".neoforge_test_datagen") != null) {
            configureNeoForgeTestDatagen(pack);
        }
    }

    public static void configureCommonDatagen(FabricDataGenerator.Pack pack) {
        FabricTagProvider.BlockTagProvider fabricBlockTagProvider = pack.addProvider(FabricBlockTagProvider::new);
        //pack.addProvider((dataOutput, registryFuture) -> new GuidebookItemTagProvider(dataOutput, registryFuture, fabricBlockTagProvider.contentsGetter()));
        //pack.addProvider(GuidebookItemModelProvider::new);
        //pack.addProvider(GuidebookRecipeProvider::new);
        //pack.addProvider(GuidebookAdvancementProvider::new);
        pack.addProvider(GuidebookLanguageProvider::new);
    }

    public static void configureFabicTestDatagen(FabricDataGenerator.Pack pack) {
        pack.addProvider(GuidebookTestLanguageProvider::new);
    }

    public static void configureNeoForgeTestDatagen(FabricDataGenerator.Pack pack) {
        pack.addProvider(GuidebookTestLanguageProvider::new);
    }

}
