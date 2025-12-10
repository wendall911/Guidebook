package handbook.platform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import handbook.common.CommonModContainer;
import handbook.common.FabricModContainer;
import handbook.event.BookDrawScreenCallback;
import handbook.integration.rei.ReiCompat;
import handbook.network.FabricMessageOpenBookGui;
import handbook.network.FabricMessageReloadBookContents;
import handbook.platform.services.IBookHelper;

public class FabricBookHelper implements IBookHelper {

    @Override
    public void fireDrawBookScreen(Identifier book, Screen gui, int mouseX, int mouseY, float partialTicks, GuiGraphics graphics) {
        BookDrawScreenCallback.EVENT.invoker().trigger(book, gui, mouseX, mouseY, partialTicks, graphics);
    }

    @Override
    public void sendReloadContentsMessage(MinecraftServer server) {
        FabricMessageReloadBookContents.sendToAll(server);
    }

    @Override
    public void sendOpenBookGui(ServerPlayer player, Identifier book, @Nullable Identifier entry, int page) {
        FabricMessageOpenBookGui.send(player, book, entry, page);
    }

    @Override
    public Collection<CommonModContainer> getAllMods() {
        List<CommonModContainer> ret = new ArrayList<>();
        for (var mod : FabricLoader.getInstance().getAllMods()) {
            ret.add(new FabricModContainer(mod));
        }
        return ret;
    }

    @Override
    public CommonModContainer getModContainer(String modId) {
        return new FabricModContainer(FabricLoader.getInstance().getModContainer(modId).get());
    }

    @Override
    public boolean isDevEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean handleRecipeKeybind(KeyEvent keyEvent, @Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        else if (FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
            return ReiCompat.handleRecipeKeybind(keyEvent, stack);
        }

        return false;
    }

}
