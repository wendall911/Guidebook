package guidebook.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import guidebook.client.base.ClientAdvancements;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(at = @At("HEAD"), method = "disconnect")
    public void guidebook$onLogout(Screen nextScreen, boolean keepResourcePacks, CallbackInfo ci) {
        ClientAdvancements.playerLogout();
    }


}
