package guidebook;

import net.minecraft.resources.ResourceLocation;

import technology.roughness.whitenoise.platform.Services;

import guidebook.config.ConfigHandler;

public class Guidebook {
   
	public static ResourceLocation prefix(String path) {
        return loc(MODID, path);
    }

    public static void initConfig() {
        if (Services.PLATFORM.isPhysicalClient()) {
            WhiteNoiseConfigLoader.add(WhiteNoiseConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC, MODID);
        }
    }

}
