package guidebook;

import guidebook.api.GuidebookAPI;
import guidebook.config.GuidebookTestConfig;
import net.fabricmc.api.ModInitializer;
import technology.roughness.whitenoise.config.WhiteNoiseConfig;
import technology.roughness.whitenoise.config.WhiteNoiseConfigLoader;
import technology.roughness.whitenoise.platform.Services;

public class GuidebookTestFabric implements ModInitializer {

    @Override
    public void onInitialize() {

    }

    public static void initConfig() {
        if (Services.PLATFORM.isPhysicalClient()) {
            WhiteNoiseConfigLoader.add(WhiteNoiseConfig.Type.COMMON, GuidebookTestConfig.COMMON_SPEC, GuidebookAPI.MODID);
        }
    }

}
