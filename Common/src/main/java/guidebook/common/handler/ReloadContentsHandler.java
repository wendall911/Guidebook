package guidebook.common.handler;

import net.minecraft.server.MinecraftServer;

import guidebook.api.GuidebookAPI;
import guidebook.common.IBookHelper;

public class ReloadContentsHandler {

	public static void dataReloaded(MinecraftServer server) {
		// Also reload contents when someone types /reload
		GuidebookAPI.LOGGER.info("Sending reload packet to clients");
		IBookHelper.INSTANCE.sendReloadContentsMessage(server);
	}

}
