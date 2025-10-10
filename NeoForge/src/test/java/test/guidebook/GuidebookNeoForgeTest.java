package test.guidebook;

import guidebook.config.GuidebookTestConfig;
import net.neoforged.fml.common.Mod;
import technology.roughness.whitenoise.config.WhiteNoiseConfig;
import technology.roughness.whitenoise.config.WhiteNoiseConfigLoader;

@Mod("guidebooktest")
public class GuidebookNeoForgeTest {

    public GuidebookNeoForgeTest() {
        WhiteNoiseConfigLoader.add(WhiteNoiseConfig.Type.COMMON, GuidebookTestConfig.COMMON_SPEC, "guidebooktest");
    }

}
