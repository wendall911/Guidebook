package handbook;

import technology.roughness.whitenoise.config.WhiteNoiseConfigInitializer;

public class TestFabricConfigInitializer implements WhiteNoiseConfigInitializer {

    @Override
    public void onInitializeConfig() {
        HandbookTestFabric.initConfig();
    }

}
