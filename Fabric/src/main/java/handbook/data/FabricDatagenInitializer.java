package handbook.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

import handbook.api.HandbookAPI;
import handbook.data.test.HandbookTestLanguageProvider;
import handbook.data.test.HandbookTestingBooksProvider;

public class FabricDatagenInitializer implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        if (System.getProperty(HandbookAPI.MODID + ".common_datagen") != null) {
            configureCommonDatagen(pack);
        }
        else if (System.getProperty(HandbookAPI.MODID + ".fabric_test_datagen") != null) {
            configureFabicTestDatagen(pack);
        }
        else if (System.getProperty(HandbookAPI.MODID + ".neoforge_test_datagen") != null) {
            configureNeoForgeTestDatagen(pack);
        }
    }

    public static void configureCommonDatagen(FabricDataGenerator.Pack pack) {
        pack.addProvider(HandbookItemTagsProvider::new);
        pack.addProvider(HandbookItemModelProvider::new);
        pack.addProvider(HandbookLanguageProvider::new);
        pack.addProvider(HandbookInternalBookProvider::new);
    }

    public static void configureFabicTestDatagen(FabricDataGenerator.Pack pack) {
        pack.addProvider(HandbookTestLanguageProvider::new);
        pack.addProvider(HandbookTestingBooksProvider::new);
    }

    public static void configureNeoForgeTestDatagen(FabricDataGenerator.Pack pack) {
        pack.addProvider(HandbookTestLanguageProvider::new);
        pack.addProvider(HandbookTestingBooksProvider::new);
    }

}
