package guidebook;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
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
import guidebook.network.NeoForgeNetworkHandler;

@Mod(GuidebookAPI.MOD_ID)
public class GuidebookNeoForge {

	public NeoForgeModInitializer(IEventBus eventBus) {
		eventBus.addListener(NeoForgeNetworkHandler::setupPackets);
        NeoForge.EVENT_BUS.addListener(GuidebookNeoForge::register);
        NeoForge.EVENT_BUS.addListener(GuidebookNeoForge::processCreativeTabs);
        NeoForge.EVENT_BUS.addListener(GuidebookNeoForge::onInitialize);
        NeoForge.EVENT_BUS.addListener(GuidebookNeoForge::);
	}

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

	public static void processCreativeTabs(BuildCreativeModeTabContentsEvent event) {
		BookRegistry.INSTANCE.books.values().forEach(b -> {
			if (!b.noBook) {
				ItemStack book = ItemModBook.forBook(b);
				if (event.getTabKey() == CreativeModeTabs.SEARCH) {
					if (!event.getSearchEntries().contains(book)) {
						event.accept(book, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
					}
				} else if (b.creativeTab != null) {
					if (event.getTab() == CreativeModeTabRegistry.getTab(b.creativeTab)) {
						event.accept(book);
					}
				}
			}
		});
	}

	public static void onInitialize(FMLCommonSetupEvent event) {
		NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> OpenBookCommand.register(e.getDispatcher()));
		NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) -> {
			var result = LecternEventHandler.rightClick(e.getEntity(), e.getLevel(), e.getHand(), e.getHitVec());
			if (result.consumesAction()) {
				e.setCanceled(true);
				e.setCancellationResult(result);
			}
		});

		BookRegistry.INSTANCE.init();

		NeoForge.EVENT_BUS.addListener((ServerStartedEvent e) -> ReloadContentsHandler.dataReloaded(e.getServer()));
	}

}
