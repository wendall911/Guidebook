package handbook.network.handler;

import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import handbook.common.util.ServerRecipeUtil;
import handbook.network.FetchRecipe;

public class NeoForgeServerPayloadHandler {

    private static final NeoForgeServerPayloadHandler INSTANCE = new NeoForgeServerPayloadHandler();

    public static NeoForgeServerPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void processFetchRecipe(FetchRecipe data, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerRecipeUtil.processFetchRecipe(data, (ServerPlayer) context.player());
        });
    }

}
