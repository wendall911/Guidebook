package handbook;

import handbook.network.NeoForgeNetworkHandler;
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

import handbook.api.HandbookAPI;
import handbook.common.advancement.HandbookCriteriaTriggers;
import handbook.common.base.HandbookSounds;
import handbook.common.book.BookRegistry;
import handbook.common.command.OpenBookCommand;
import handbook.common.handler.LecternEventHandler;
import handbook.common.handler.ReloadContentsHandler;
import handbook.common.item.HandbookBook;
import handbook.common.item.HandbookDataComponents;
import handbook.common.item.HandbookItems;

@EventBusSubscriber(modid = HandbookAPI.MODID)
@Mod(HandbookAPI.MODID)
public class HandbookNeoForge {

    public HandbookNeoForge(IEventBus eventBus) {
        Handbook.initConfig();
        eventBus.addListener(NeoForgeNetworkHandler::setupPackets);
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, rh -> {
            HandbookSounds.submitRegistrations(rh::register);
        });
        event.register(Registries.DATA_COMPONENT_TYPE, rh -> {
            HandbookDataComponents.submitDataComponentRegistrations(rh::register);
        });
        event.register(Registries.ITEM, rh -> {
            HandbookItems.submitItemRegistrations(rh::register);
        });
        event.register(Registries.TRIGGER_TYPE, rh -> HandbookCriteriaTriggers.submitTriggerRegistrations(rh::register));
    }

    @SubscribeEvent
    public static void processCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        BookRegistry.INSTANCE.books.values().forEach(b -> {
            if (!b.noBook) {
                ItemStack book = HandbookBook.forBook(b);

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

        BookRegistry.INSTANCE.init();

        NeoForge.EVENT_BUS.addListener((ServerStartedEvent e) -> ReloadContentsHandler.dataReloaded(e.getServer()));
    }

}
