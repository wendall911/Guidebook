package guidebook;

import guidebook.network.NeoForgeNetworkHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.CreativeModeTabRegistry;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import guidebook.api.GuidebookAPI;
import guidebook.common.advancement.GuidebookCriteriaTriggers;
import guidebook.common.base.GuidebookSounds;
import guidebook.common.book.BookRegistry;
import guidebook.common.command.OpenBookCommand;
import guidebook.common.handler.LecternEventHandler;
import guidebook.common.handler.ReloadContentsHandler;
import guidebook.common.item.ItemModBook;
import guidebook.common.item.GuidebookDataComponents;
import guidebook.common.item.GuidebookItems;

@EventBusSubscriber(modid = GuidebookAPI.MODID)
@Mod(GuidebookAPI.MODID)
public class GuidebookNeoForge {

    public GuidebookNeoForge(IEventBus eventBus) {
        Guidebook.initConfig();
        BookRegistry.INSTANCE.init();
        eventBus.addListener(NeoForgeNetworkHandler::setupPackets);
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, rh -> {
            GuidebookSounds.submitRegistrations(rh::register);
        });
        event.register(Registries.DATA_COMPONENT_TYPE, rh -> {
            GuidebookDataComponents.submitDataComponentRegistrations(rh::register);
        });
        event.register(Registries.ITEM, rh -> {
            GuidebookItems.submitItemRegistrations(rh::register);
        });
        event.register(Registries.TRIGGER_TYPE, rh -> GuidebookCriteriaTriggers.submitTriggerRegistrations(rh::register));
    }

    @SubscribeEvent
    public static void processCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        BookRegistry.INSTANCE.books.values().forEach(b -> {
            if (!b.noBook) {
                ItemStack book = ItemModBook.forBook(b);

                if (event.getTabKey() == CreativeModeTabs.SEARCH) {
                    if (!event.getSearchEntries().contains(book)) {
                        event.accept(book, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
                    }
                }
                else if (b.creativeTab != null) {
                    if (event.getTab() == CreativeModeTabRegistry.getTab(b.creativeTab)) {
                        event.accept(book);
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public static void onInitialize(FMLCommonSetupEvent event) {
        NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> OpenBookCommand.register(e.getDispatcher()));
        NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
            InteractionResult result = LecternEventHandler.rightClick(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());

            if (result.consumesAction()) {
                e.setCanceled(true);
                e.setCancellationResult(result);
            }
        });

        NeoForge.EVENT_BUS.addListener((ServerStartedEvent e) -> ReloadContentsHandler.dataReloaded(e.getServer()));
    }

}
