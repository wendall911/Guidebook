package handbook.mixin.client;

import java.util.List;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import handbook.client.gui.GuiButtonInventoryBook;
import handbook.common.book.Book;
import handbook.common.book.BookRegistry;
import handbook.config.HandbookConfig;

@Mixin(InventoryScreen.class)
public abstract class MixinInventoryScreen extends AbstractRecipeBookScreen<InventoryMenu> {

    public MixinInventoryScreen(Player player, Component text) {
        super(player.inventoryMenu, new CraftingRecipeBookComponent(player.inventoryMenu), player.getInventory(), text);
    }

    @SuppressWarnings("unchecked")
    @Inject(at = @At("RETURN"), method = "init()V")
    public void onGuiInitPost(CallbackInfo info) {
        ResourceLocation bookID = ResourceLocation.tryParse(HandbookConfig.Client.inventoryButtonBook());
        Book book = BookRegistry.INSTANCE.books.get(bookID);
        Renderable replaced = null;
        Button replacement = null;

        if (book == null) {
            return;
        }

        for (int i = 0; i < ((AccessorScreen) this).getRenderables().size(); i++) {
            Renderable button = ((AccessorScreen) this).getRenderables().get(i);

            if (button instanceof ImageButton tex) {
                replaced = button;
                replacement = new GuiButtonInventoryBook(book, tex.getX(), tex.getY() - 1);
                ((AccessorScreen) this).getRenderables().set(i, replacement);
                break;
            }
        }

        int i = children().indexOf(replaced);
        if (i >= 0) {
            ((List<GuiEventListener>) children()).set(i, replacement);
        }

        i = ((AccessorScreen) this).getNarratables().indexOf(replaced);
        if (i >= 0) {
            ((AccessorScreen) this).getNarratables().set(i, replacement);
        }
    }

}
