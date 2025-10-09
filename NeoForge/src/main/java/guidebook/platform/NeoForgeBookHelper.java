package guidebook.platform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

import guidebook.common.CommonModContainer;
import guidebook.common.NeoForgeModContainer;
import guidebook.event.BookDrawScreenEvent;
import guidebook.network.NeoForgeNetworkHandler;
import guidebook.GuidebookClientNeoForge;
import guidebook.platform.services.IBookHelper;

public class NeoForgeBookHelper implements IBookHelper {

    @Override
    public void fireDrawBookScreen(ResourceLocation book, Screen gui, int mouseX, int mouseY, float partialTicks, GuiGraphics graphics) {
        NeoForge.EVENT_BUS.post(new BookDrawScreenEvent(book, gui, mouseX, mouseY, partialTicks, graphics));
    }

    @Override
    public void sendReloadContentsMessage(MinecraftServer server) {
        NeoForgeNetworkHandler.sendReloadBookContents(server);
    }

    @Override
    public void sendOpenBookGui(ServerPlayer player, ResourceLocation book, @Nullable ResourceLocation entry, int page) {
        NeoForgeNetworkHandler.sendOpenBook(player, book, entry, page);
    }

    @Override
    public Collection<CommonModContainer> getAllMods() {
        List<CommonModContainer> ret = new ArrayList<>();
        for (var info : ModList.get().getMods()) {
            ret.add(new NeoForgeModContainer(ModList.get().getModContainerById(info.getModId()).get()));
        }
        return ret;
    }

    @Override
    public CommonModContainer getModContainer(String modId) {
        return new NeoForgeModContainer(ModList.get().getModContainerById(modId).get());
    }

    @Override
    public boolean isDevEnvironment() {
        return !FMLEnvironment.production;
    }

    @Override
    public void signalBooksLoaded() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            GuidebookClientNeoForge.signalBooksLoaded();
        }
    }

    @Override
    public boolean handleRecipeKeybind(int keyCode, int scanCode, @Nullable ItemStack stack) {
        return false;
    }

}
