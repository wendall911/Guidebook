package handbook;

import handbook.api.HandbookAPI;
import handbook.config.HandbookTestConfig;
import net.fabricmc.api.ModInitializer;
import technology.roughness.whitenoise.config.WhiteNoiseConfig;
import technology.roughness.whitenoise.config.WhiteNoiseConfigLoader;

public class HandbookTestFabric implements ModInitializer {

    @Override
    public void onInitialize() {

    }

    public static void initConfig() {
        WhiteNoiseConfigLoader.add(WhiteNoiseConfig.Type.COMMON, HandbookTestConfig.COMMON_SPEC, "handbooktest");
    }

}
