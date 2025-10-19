package test.handbook;

import handbook.config.HandbookTestConfig;
import net.neoforged.fml.common.Mod;
import technology.roughness.whitenoise.config.WhiteNoiseConfig;
import technology.roughness.whitenoise.config.WhiteNoiseConfigLoader;

@Mod("handbooktest")
public class HandbookNeoForgeTest {

    public HandbookNeoForgeTest() {
        WhiteNoiseConfigLoader.add(WhiteNoiseConfig.Type.COMMON, HandbookTestConfig.COMMON_SPEC, "handbooktest");
    }

}
