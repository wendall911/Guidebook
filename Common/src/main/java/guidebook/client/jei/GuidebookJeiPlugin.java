package guidebook.client.jei;

import java.util.Map;

import org.jetbrains.annotations.NotNull;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import guidebook.api.GuidebookAPI;
import guidebook.common.item.GuidebookDataComponents;
import guidebook.common.item.GuidebookItems;
import guidebook.mixin.client.AccessorKeyMapping;

@JeiPlugin
public class GuidebookJeiPlugin implements IModPlugin {

	private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(GuidebookAPI.MODID, GuidebookAPI.MODID);

	private static final KeyMapping showRecipe, showUses;

	private static IJeiRuntime jeiRuntime;

	static {
		Map<String, KeyMapping> allKeyMappings = AccessorKeyMapping.getAllKeyMappings();
		showRecipe = allKeyMappings.get("key.jei.showRecipe");
		showUses = allKeyMappings.get("key.jei.showUses");
		if (showRecipe == null || showUses == null) {
			GuidebookAPI.LOGGER.warn("Could not locate JEI keybindings, lookups in books may not work");
		}
	}

	@NotNull
	@Override
	public ResourceLocation getPluginUid() {
		return UID;
	}

	@Override
	public void registerItemSubtypes(@NotNull ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, GuidebookItems.BOOK, (stack, context) -> {
			if (!stack.has(GuidebookDataComponents.BOOK)) {
				return "";
			}
			return stack.get(GuidebookDataComponents.BOOK).toString();
		});
	}

	@Override
	public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
		GuidebookJeiPlugin.jeiRuntime = jeiRuntime;
	}

	public static boolean handleRecipeKeybind(int keyCode, int scanCode, ItemStack stack) {
		if (showRecipe != null && showRecipe.matches(keyCode, scanCode)) {
			var focus = jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, stack);
			jeiRuntime.getRecipesGui().show(focus);
			return true;
		}
		if (showUses != null && showUses.matches(keyCode, scanCode)) {
			var focus = jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.INPUT, VanillaTypes.ITEM_STACK, stack);
			jeiRuntime.getRecipesGui().show(focus);
			return true;
		}
		return false;
	}
}
