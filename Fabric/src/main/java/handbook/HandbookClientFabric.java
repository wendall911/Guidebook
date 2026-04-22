package handbook;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;

import net.minecraft.server.packs.PackType;

import handbook.api.HandbookAPI;
import handbook.client.base.ClientTicker;
import handbook.client.base.PersistentData;
import handbook.client.base.RecipeData;
import handbook.client.book.BookContentResourceListenerLoader;
import handbook.client.book.ClientBookRegistry;
import handbook.client.handler.BookRightClickHandler;
import handbook.network.FabricMessageOpenBookGui;
import handbook.network.FabricMessageReloadBookContents;
import handbook.network.MessageOpenBookGui;
import handbook.network.MessageReloadBookContents;
import handbook.network.SendRecipe;

public class HandbookClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientBookRegistry.INSTANCE.init();
        PersistentData.setup();
        ClientTickEvents.END_CLIENT_TICK.register(ClientTicker::endClientTick);
        HudElementRegistry.addLast(HandbookAPI.prefix("book_hud"), BookRightClickHandler::onRenderHUD);
        UseBlockCallback.EVENT.register(BookRightClickHandler::onRightClick);
        ClientPlayNetworking.registerGlobalReceiver(MessageOpenBookGui.TYPE, FabricMessageOpenBookGui::handle);
        ClientPlayNetworking.registerGlobalReceiver(
            MessageReloadBookContents.TYPE,
            FabricMessageReloadBookContents::handle
        );
        ClientPlayNetworking.registerGlobalReceiver(SendRecipe.TYPE, (data,  context) -> {
            RecipeData.setRecipe(data.recipeId(), data.recipe());
        });
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
            BookContentResourceListenerLoader.ID,
            BookContentResourceListenerLoader.INSTANCE
        );
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
            ClientBookRegistry.ID,
            ClientBookRegistry.INSTANCE
        );
    }

}
