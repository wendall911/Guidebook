package guidebook.common.platform.services;

import java.util.Collection;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import guidebook.api.GuidebookAPI;

/**
 * Cross-modloader abstracted calls
 */
public interface IBookHelper {

	// Events
	void fireDrawBookScreen(ResourceLocation book, Screen gui, int mouseX, int mouseY, float partialTicks, GuiGraphics graphics);
	void fireBookReload(ResourceLocation book);

	// Networking
	void sendReloadContentsMessage(MinecraftServer server);
	void sendOpenBookGui(ServerPlayer player, ResourceLocation book, @Nullable ResourceLocation entry, int page);

	// FML/FabricLoader-related
	Collection<XplatModContainer> getAllMods();
	XplatModContainer getModContainer(String modId);
	boolean isModLoaded(String modId);
	boolean isDevEnvironment();

	// Misc
	boolean isPhysicalClient();

	// Needed because of Forge
	default void signalBooksLoaded() {}

	// JEI/REI compat
	boolean handleRecipeKeybind(int keyCode, int scanCode, ItemStack stack);

	IBookHelper INSTANCE = find();

	private static IBookHelper find() {
		var providers = ServiceLoader.load(IBookHelper.class, IBookHelper.class.getClassLoader()).stream().toList();
		if (providers.size() != 1) {
			var names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
			throw new IllegalStateException("There should be exactly one IBookHelper implementation on the classpath. Found: " + names);
		} else {
			var provider = providers.get(0);
			GuidebookAPI.LOGGER.debug("Instantiating common impl: " + provider.type().getName());
			return provider.get();
		}
	}

}
