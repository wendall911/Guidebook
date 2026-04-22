package handbook.network.handler;

import net.minecraft.network.chat.Component;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import handbook.api.HandbookAPI;
import handbook.client.base.RecipeData;
import handbook.client.book.ClientBookRegistry;
import handbook.network.MessageOpenBookGui;
import handbook.network.MessageReloadBookContents;
import handbook.network.SendRecipe;

public class NeoForgeClientPayloadHandler {

    private static final NeoForgeClientPayloadHandler INSTANCE = new NeoForgeClientPayloadHandler();

    public static NeoForgeClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleData(final MessageOpenBookGui data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                ClientBookRegistry.INSTANCE.displayBookGui(data.book(), data.entry(), data.page());
            }
            catch (Exception e) {
                context.disconnect(Component.translatable("handbook.networking.open_book.failed", e.getMessage()));
            }
        });
    }

    public void handleData(final MessageReloadBookContents data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                ClientBookRegistry.INSTANCE.reload();
            }
            catch (Exception e) {
                context.disconnect(Component.translatable("handbook.networking.reload_contents.failed", e.getMessage()));
            }
        });
    }

    public void processRecipe(final SendRecipe data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            RecipeData.setRecipe(data.recipeId(), data.recipe());
        });
    }

}
