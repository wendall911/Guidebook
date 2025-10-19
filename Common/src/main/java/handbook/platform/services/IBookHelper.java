package handbook.platform.services;

import java.util.Collection;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import handbook.common.CommonModContainer;

/**
 * Cross-modloader abstracted calls
 */
public interface IBookHelper {

    // Events
    void fireDrawBookScreen(ResourceLocation book, Screen gui, int mouseX, int mouseY, float partialTicks, GuiGraphics graphics);

    // Networking
    void sendReloadContentsMessage(MinecraftServer server);
    void sendOpenBookGui(ServerPlayer player, ResourceLocation book, @Nullable ResourceLocation entry, int page);

    // FML/FabricLoader-related
    Collection<CommonModContainer> getAllMods();
    CommonModContainer getModContainer(String modId);

    boolean isDevEnvironment();

    // Needed because of Forge
    default void signalBooksLoaded() {}

    // JEI/REI compat
    boolean handleRecipeKeybind(int keyCode, int scanCode, ItemStack stack);

}
