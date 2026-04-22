package handbook.platform.services;

import java.util.Collection;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import handbook.common.CommonModContainer;

/**
 * Cross-modloader abstracted calls
 */
public interface IBookHelper {

    // Events
    void fireDrawBookScreen(Identifier book, Screen gui, int mouseX, int mouseY, float partialTicks, GuiGraphicsExtractor graphics);

    // Networking
    void sendReloadContentsMessage(MinecraftServer server);
    void sendOpenBookGui(ServerPlayer player, Identifier book, @Nullable Identifier entry, int page);
    void fetchRecipe(Identifier recipeId);
    void sendRecipe(ServerPlayer player, Identifier recipeId, RecipeHolder<?> recipe);

    // FML/FabricLoader-related
    Collection<CommonModContainer> getAllMods();
    CommonModContainer getModContainer(String modId);

    boolean isDevEnvironment();

    // JEI/REI compat
    boolean handleRecipeKeybind(KeyEvent keyEvent, ItemStack stack);

}
