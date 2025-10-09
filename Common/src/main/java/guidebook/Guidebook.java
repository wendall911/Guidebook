package guidebook;

import technology.roughness.whitenoise.config.WhiteNoiseConfig;
import technology.roughness.whitenoise.config.WhiteNoiseConfigLoader;
import technology.roughness.whitenoise.platform.Services;

import guidebook.api.GuidebookAPI;
import guidebook.config.GuidebookConfig;

public class Guidebook {

    public static void initConfig() {
        if (Services.PLATFORM.isPhysicalClient()) {
            WhiteNoiseConfigLoader.add(WhiteNoiseConfig.Type.COMMON, GuidebookConfig.CLIENT_SPEC, GuidebookAPI.MODID);
        }
    }

}
