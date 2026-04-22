package handbook;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

import handbook.common.advancement.HandbookCriteriaTriggers;
import handbook.common.base.HandbookSounds;
import handbook.common.book.BookRegistry;
import handbook.common.command.OpenBookCommand;
import handbook.common.handler.LecternEventHandler;
import handbook.common.handler.ReloadContentsHandler;
import handbook.common.item.HandbookBook;
import handbook.common.item.HandbookDataComponents;
import handbook.common.item.HandbookItems;
import handbook.common.util.ServerRecipeUtil;
import handbook.network.FetchRecipe;
import handbook.network.MessageOpenBookGui;
import handbook.network.MessageReloadBookContents;
import handbook.network.SendRecipe;

public class HandbookFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        HandbookSounds.submitRegistrations((id, e) -> Registry.register(BuiltInRegistries.SOUND_EVENT, id, e));
        HandbookDataComponents.submitDataComponentRegistrations((id, e) -> Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, e));
        HandbookItems.submitItemRegistrations((id, e) -> Registry.register(BuiltInRegistries.ITEM, id, e));
        HandbookCriteriaTriggers.submitTriggerRegistrations((id, e) -> Registry.register(BuiltInRegistries.TRIGGER_TYPES, id, e));
        CommandRegistrationCallback.EVENT.register((disp, buildCtx, selection) -> OpenBookCommand.register(disp));
        UseBlockCallback.EVENT.register(LecternEventHandler::rightClick);

        PayloadTypeRegistry.clientboundPlay().register(MessageOpenBookGui.TYPE, MessageOpenBookGui.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MessageReloadBookContents.TYPE, MessageReloadBookContents.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(FetchRecipe.TYPE, FetchRecipe.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SendRecipe.TYPE, SendRecipe.STREAM_CODEC);

        ServerPlayNetworking.registerGlobalReceiver(FetchRecipe.TYPE, ((data, context) -> {
            ServerRecipeUtil.processFetchRecipe(data, context.player());
        }));

        BookRegistry.INSTANCE.init();

        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, _r, success) -> {
            if (success) {
                ReloadContentsHandler.dataReloaded(server);
            }
        });

        BookRegistry.INSTANCE.books.values().forEach(b -> {
            if (!b.noBook) {
                if (b.creativeTab != null) {
                    ResourceKey<CreativeModeTab> key = ResourceKey.create(Registries.CREATIVE_MODE_TAB, b.creativeTab);
                    CreativeModeTabEvents.modifyOutputEvent(key)
                        .register(entries -> entries.accept(HandbookBook.forBook(b)));
                }
                CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SEARCH).register(entries -> {
                    entries.accept(HandbookBook.forBook(b));
                });
            }
        });
    }

}
