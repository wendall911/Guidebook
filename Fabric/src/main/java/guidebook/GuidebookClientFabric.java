package guidebook;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import guidebook.api.GuidebookAPI;
import guidebook.client.base.BookModel;
import guidebook.client.base.ClientTicker;
import guidebook.client.base.PersistentData;
import guidebook.client.book.BookContentResourceListenerLoader;
import guidebook.client.book.ClientBookRegistry;
import guidebook.client.handler.BookRightClickHandler;
import guidebook.common.book.Book;
import guidebook.common.book.BookRegistry;
import guidebook.common.item.ItemModBook;
import guidebook.common.item.GuidebookItems;
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
        HudRenderCallback.EVENT.register(BookRightClickHandler::onRenderHUD);
        UseBlockCallback.EVENT.register(BookRightClickHandler::onRightClick);
        ClientPlayNetworking.registerGlobalReceiver(MessageOpenBookGui.TYPE, FabricMessageOpenBookGui::handle);
        ClientPlayNetworking.registerGlobalReceiver(MessageReloadBookContents.TYPE, FabricMessageReloadBookContents::handle);

        ModelLoadingPlugin.register(pluginContext -> {
            for (Book book : BookRegistry.INSTANCE.books.values()) {
                GuidebookAPI.LOGGER.info("Adding model {}", book.model);
                pluginContext.addModels(book.model);
            }

            pluginContext.modifyModelAfterBake().register(
                    (oldModel, ctx) -> {
                        if (ctx.topLevelId() != null &&
                                GuidebookItems.BOOK_ID.equals(ctx.topLevelId().id()) // checks namespace and path
                                && ctx.topLevelId().getVariant().equals("inventory")
                                && oldModel != null) {
                            return new BookModel(oldModel, (model) -> Minecraft.getInstance().getModelManager().getModel(model));
                        }
                        return oldModel;
                    }
            );
        });

        ItemProperties.register(GuidebookItems.BOOK,
                GuidebookAPI.prefix("completion"),
                (stack, world, entity, seed) -> ItemModBook.getCompletion(stack));

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new IdentifiableResourceReloadListener() {
            private static final ResourceLocation id = GuidebookAPI.prefix("resource_pack_books");

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager manager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                return BookContentResourceListenerLoader.INSTANCE.reload(barrier, manager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
            }

            @Override
            public ResourceLocation getFabricId() {
                return id;
            }
        });
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            private static final ResourceLocation id = GuidebookAPI.prefix("reload_hook");

            @Override
            public ResourceLocation getFabricId() {
                return id;
            }

            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                if (Minecraft.getInstance().level != null) {
                    GuidebookAPI.LOGGER.info("Reloading resource pack-based books");
                    ClientBookRegistry.INSTANCE.reload();
                } else {
                    GuidebookAPI.LOGGER.debug("Not reloading resource pack-based books as client world is missing");
                }
            }
        });
    }

}
