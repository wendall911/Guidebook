package handbook.client.jei;

import java.util.Map;

import org.jspecify.annotations.NonNull;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import handbook.api.HandbookAPI;
import handbook.common.item.HandbookDataComponents;
import handbook.common.item.HandbookItems;
import handbook.mixin.client.AccessorKeyMapping;

@JeiPlugin
public class HandbookJeiPlugin implements IModPlugin {

    private static final Identifier UID = Identifier.fromNamespaceAndPath(HandbookAPI.MODID, HandbookAPI.MODID);

    private static KeyMapping showRecipe, showUses;

    private static IJeiRuntime jeiRuntime;

    static {
    }

    @NonNull
    @Override
    public Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerItemSubtypes(@NonNull ISubtypeRegistration registration) {
        ISubtypeInterpreter<ItemStack> bookInterpreter = (stack, context) -> stack.get(HandbookDataComponents.BOOK);

        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, HandbookItems.BOOK, bookInterpreter);
    }

    @Override
    public void onRuntimeAvailable(@NonNull IJeiRuntime jeiRuntime) {
        HandbookJeiPlugin.jeiRuntime = jeiRuntime;

        Map<String, KeyMapping> allKeyMappings = AccessorKeyMapping.getAllKeyMappings();

        HandbookJeiPlugin.showRecipe = allKeyMappings.get("key.jei.showRecipe");
        HandbookJeiPlugin.showUses = allKeyMappings.get("key.jei.showUses");

        if (showRecipe == null || showUses == null) {
            HandbookAPI.LOGGER.warn("Could not locate JEI keybindings, lookups in books may not work");
        }
    }

    public static boolean handleRecipeKeybind(KeyEvent keyEvent, ItemStack stack) {
        IFocus<ItemStack> focus;

        if (showRecipe != null && showRecipe.matches(keyEvent)) {
            focus = jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.OUTPUT, VanillaTypes.ITEM_STACK, stack);

            jeiRuntime.getRecipesGui().show(focus);

            return true;
        }
        else if (showUses != null && showUses.matches(keyEvent)) {
            focus = jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.INPUT, VanillaTypes.ITEM_STACK, stack);

            jeiRuntime.getRecipesGui().show(focus);

            return true;
        }

        return false;
    }

}
