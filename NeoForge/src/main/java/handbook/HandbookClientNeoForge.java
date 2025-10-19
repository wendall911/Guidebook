package handbook;

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

import handbook.api.HandbookAPI;
import handbook.client.base.ClientAdvancements;
import handbook.client.base.ClientTicker;
import handbook.client.base.PersistentData;
import handbook.client.book.BookContentResourceListenerLoader;
import handbook.client.book.ClientBookRegistry;
import handbook.client.handler.BookRightClickHandler;
import handbook.client.handler.TooltipHandler;

@EventBusSubscriber(modid = HandbookAPI.MODID, value = Dist.CLIENT)
public class HandbookClientNeoForge {

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
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, HandbookAPI.prefix("book_overlay"),
            BookRightClickHandler::onRenderHUD
        );
    }

}
