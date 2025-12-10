package handbook.platform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforgespi.language.IModInfo;

import handbook.common.CommonModContainer;
import handbook.common.NeoForgeModContainer;
import handbook.event.BookDrawScreenEvent;
import handbook.network.NeoForgeNetworkHandler;
import handbook.platform.services.IBookHelper;

public class NeoForgeBookHelper implements IBookHelper {

    @Override
    public void fireDrawBookScreen(Identifier book, Screen gui, int mouseX, int mouseY, float partialTicks, GuiGraphics graphics) {
        NeoForge.EVENT_BUS.post(new BookDrawScreenEvent(book, gui, mouseX, mouseY, partialTicks, graphics));
    }

    @Override
    public void sendReloadContentsMessage(MinecraftServer server) {
        NeoForgeNetworkHandler.sendReloadBookContents(server);
    }

    @Override
    public void sendOpenBookGui(ServerPlayer player, Identifier book, @Nullable Identifier entry, int page) {
        NeoForgeNetworkHandler.sendOpenBook(player, book, entry, page);
    }

    @Override
    public Collection<CommonModContainer> getAllMods() {
        List<CommonModContainer> ret = new ArrayList<>();

        for (IModInfo info : ModList.get().getMods()) {
            ret.add(new NeoForgeModContainer(ModList.get().getModContainerById(info.getModId()).orElseThrow()));
        }

        return ret;
    }

    @Override
    public CommonModContainer getModContainer(String modId) {
        return new NeoForgeModContainer(ModList.get().getModContainerById(modId).orElseThrow());
    }

    @Override
    public boolean isDevEnvironment() {
        return !FMLEnvironment.isProduction();
    }

    @Override
    public boolean handleRecipeKeybind(KeyEvent keyEvent, @Nullable ItemStack stack) {
        return false;
    }

}
