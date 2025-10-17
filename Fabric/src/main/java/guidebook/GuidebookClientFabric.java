package guidebook;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;

import guidebook.api.GuidebookAPI;
import guidebook.client.base.ClientTicker;
import guidebook.client.base.PersistentData;
import guidebook.client.book.BookContentResourceListenerLoader;
import guidebook.client.book.ClientBookRegistry;
import guidebook.client.handler.BookRightClickHandler;
import guidebook.network.FabricMessageOpenBookGui;
import guidebook.network.FabricMessageReloadBookContents;
import guidebook.network.MessageOpenBookGui;
import guidebook.network.MessageReloadBookContents;

public class GuidebookClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientBookRegistry.INSTANCE.init();
        PersistentData.setup();
        ClientTickEvents.END_CLIENT_TICK.register(ClientTicker::endClientTick);
        HudElementRegistry.addLast(GuidebookAPI.prefix("book_hud"), BookRightClickHandler::onRenderHUD);
        UseBlockCallback.EVENT.register(BookRightClickHandler::onRightClick);
        ClientPlayNetworking.registerGlobalReceiver(MessageOpenBookGui.TYPE, FabricMessageOpenBookGui::handle);
        ClientPlayNetworking.registerGlobalReceiver(
            MessageReloadBookContents.TYPE,
            FabricMessageReloadBookContents::handle
        );
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(
            BookContentResourceListenerLoader.ID,
            BookContentResourceListenerLoader.INSTANCE
        );
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(
            ClientBookRegistry.ID,
            ClientBookRegistry.INSTANCE
        );
    }

}
