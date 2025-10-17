package guidebook;

import net.minecraft.client.Minecraft;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import guidebook.api.GuidebookAPI;
import guidebook.client.base.ClientAdvancements;
import guidebook.client.base.ClientTicker;
import guidebook.client.base.PersistentData;
import guidebook.client.book.BookContentResourceListenerLoader;
import guidebook.client.book.ClientBookRegistry;
import guidebook.client.handler.BookRightClickHandler;
import guidebook.client.handler.TooltipHandler;

@EventBusSubscriber(modid = GuidebookAPI.MODID, value = Dist.CLIENT)
public class GuidebookClientNeoForge {

    @SubscribeEvent
    public static void onInitializeClient(FMLClientSetupEvent event) {
        ClientBookRegistry.INSTANCE.init();
        PersistentData.setup();
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> {
            ClientTicker.endClientTick(Minecraft.getInstance());
        });
        NeoForge.EVENT_BUS.addListener(
            (PlayerInteractEvent.RightClickBlock e)
                -> BookRightClickHandler.onRightClick(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec())
        );
        NeoForge.EVENT_BUS.addListener((RenderFrameEvent.Pre e) -> {
            ClientTicker.renderTickStart(e.getPartialTick().getGameTimeDeltaPartialTick(false));
        });
        NeoForge.EVENT_BUS.addListener((RenderFrameEvent.Post e) -> {
            ClientTicker.renderTickEnd();
        });
        NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut e) -> {
            ClientAdvancements.playerLogout();
        });
        NeoForge.EVENT_BUS.addListener((RenderTooltipEvent.Pre e) -> {
            TooltipHandler.onTooltip(e.getGraphics(), e.getItemStack(), e.getX(), e.getY());
        });
    }

    @SubscribeEvent
    public static void registerReloadListeners(AddClientReloadListenersEvent event) {
        event.addListener(BookContentResourceListenerLoader.ID, BookContentResourceListenerLoader.INSTANCE);
        event.addListener(ClientBookRegistry.ID, ClientBookRegistry.INSTANCE);
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, GuidebookAPI.prefix("book_overlay"),
            BookRightClickHandler::onRenderHUD
        );
    }

}
