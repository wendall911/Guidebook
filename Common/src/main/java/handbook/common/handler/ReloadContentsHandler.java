package handbook.common.handler;

import net.minecraft.server.MinecraftServer;

import handbook.api.HandbookAPI;
import handbook.platform.Services;

public class ReloadContentsHandler {

    public static void dataReloaded(MinecraftServer server) {
        // Also reload contents when someone types /reload
        HandbookAPI.LOGGER.info("Sending reload packet to clients");
        Services.BOOK_HELPER.sendReloadContentsMessage(server);
    }

}
