package handbook;

import technology.roughness.whitenoise.config.WhiteNoiseConfig;
import technology.roughness.whitenoise.config.WhiteNoiseConfigLoader;
import technology.roughness.whitenoise.platform.Services;

import handbook.api.HandbookAPI;
import handbook.config.HandbookConfig;

public class Handbook {

    public static void initConfig() {
        if (Services.WN_PLATFORM.isPhysicalClient()) {
            WhiteNoiseConfigLoader.add(WhiteNoiseConfig.Type.CLIENT, HandbookConfig.CLIENT_SPEC, HandbookAPI.MODID);
        }
    }

}
