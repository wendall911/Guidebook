package handbook.mixin.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import handbook.client.handler.TooltipHandler;

@Mixin(GuiGraphicsExtractor.class)
public class MixinGuiGraphicsExtractor {

    @Inject(at = @At("HEAD"), method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V")
    public void handbook$setTooltipForNextFrame(Font font, ItemStack stack, int x, int y, CallbackInfo info) {
        TooltipHandler.onTooltip((GuiGraphicsExtractor) (Object) this, stack, x, y);
    }

}
