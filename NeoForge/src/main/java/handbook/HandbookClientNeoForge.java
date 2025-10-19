package handbook;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import handbook.api.HandbookAPI;
import handbook.client.base.BookModel;
import handbook.client.base.ClientAdvancements;
import handbook.client.base.ClientTicker;
import handbook.client.base.PersistentData;
import handbook.client.book.BookContentResourceListenerLoader;
import handbook.client.book.ClientBookRegistry;
import handbook.client.handler.BookRightClickHandler;
import handbook.client.handler.TooltipHandler;
import handbook.common.book.BookRegistry;
import handbook.common.item.HandbookBook;
import handbook.common.item.HandbookItems;

@EventBusSubscriber(modid = HandbookAPI.MODID, value = Dist.CLIENT)
public class HandbookClientNeoForge {
    /**
     * Why are these necessary?
     * BookRegistry.init is called from CommonSetupEvent. We need the models to be known in ModelRegistryEvent.
     * However, there is no defined ordering for those events. They all run concurrently during the initial resource
     * reload.
     * We need a way of waiting for the books to become known.
     * Another critical point to note is that loading runs on a fixed-size ForkJoinPool.
     * Blocking the thread can starve loading completely.
     * Fortunately, the implementation of Condition.await for ReentrantLock uses ForkJoinPool.managedBlock,
     * which is aware of potentially blocking operations and can resize the pool accordingly.
     * If parallel mod loading didn't exist we wouldn't need any of this, but here we are :))))
     */
    private static final Lock BOOK_LOAD_LOCK = new ReentrantLock();
    private static final Condition BOOK_LOAD_CONDITION = BOOK_LOAD_LOCK.newCondition();
    private static boolean booksLoaded = false;

    @SubscribeEvent
    public static void onInitializeClient(FMLClientSetupEvent event) {
        ClientBookRegistry.INSTANCE.init();
        PersistentData.setup();
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post e) -> {
            ClientTicker.endClientTick(Minecraft.getInstance());
        });
        NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> BookRightClickHandler.onRightClick(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec()));
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

    public static void signalBooksLoaded() {
        BOOK_LOAD_LOCK.lock();
        booksLoaded = true;
        BOOK_LOAD_CONDITION.signalAll();
        BOOK_LOAD_LOCK.unlock();
    }

    private static List<ResourceLocation> getBookModels() {
        BOOK_LOAD_LOCK.lock();
        try {
            while (!booksLoaded) {
                BOOK_LOAD_CONDITION.awaitUninterruptibly();
            }
            return BookRegistry.INSTANCE.books.values().stream().map(b -> b.model).toList();
        }
        finally {
            BOOK_LOAD_LOCK.unlock();
        }
    }

    @SubscribeEvent
    public static void modelRegistry(ModelEvent.RegisterAdditional event) {
        getBookModels()
            .stream()
            .map(ModelResourceLocation::standalone)
            .forEach(event::register);

        ItemPropertyFunction prop = (stack, world, entity, seed) -> HandbookBook.getCompletion(stack);
        ItemProperties.register(HandbookItems.BOOK, HandbookAPI.prefix("completion"), prop);
    }

    @SubscribeEvent
    public static void registerReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(BookContentResourceListenerLoader.INSTANCE);

        event.registerReloadListener((ResourceManagerReloadListener) manager -> {
            if (Minecraft.getInstance().level != null) {
                HandbookAPI.LOGGER.info("Reloading resource pack-based books");
                ClientBookRegistry.INSTANCE.reload();
            }
            else {
                HandbookAPI.LOGGER.debug("Not reloading resource pack-based books as client world is missing");
            }
        });
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.CROSSHAIR, HandbookAPI.prefix("book_overlay"),
            BookRightClickHandler::onRenderHUD
        );
    }

    @SubscribeEvent
    public static void replaceBookModel(ModelEvent.ModifyBakingResult event) {
        ModelResourceLocation key = ModelResourceLocation.inventory(HandbookItems.BOOK_ID);

        event.getModels().computeIfPresent(
            key,
            (k, oldModel) -> new BookModel(
                oldModel,
                (model) -> Minecraft.getInstance().getModelManager().getModel(
                    ModelResourceLocation.standalone(model)
                )
            )
        );
    }

}
