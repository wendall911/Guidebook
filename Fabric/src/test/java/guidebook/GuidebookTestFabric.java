package guidebook;

import guidebook.api.GuidebookAPI;
import guidebook.config.GuidebookTestConfig;
import net.fabricmc.api.ModInitializer;
import technology.roughness.whitenoise.config.WhiteNoiseConfig;
import technology.roughness.whitenoise.config.WhiteNoiseConfigLoader;

public class GuidebookTestFabric implements ModInitializer {

    @Override
    public void onInitialize() {

    }

    public static void initConfig() {
        WhiteNoiseConfigLoader.add(WhiteNoiseConfig.Type.COMMON, GuidebookTestConfig.COMMON_SPEC, "guidebooktest");
    }

}
