package fr.anarchick.skb.mixin;

import fr.anarchick.skb.event.KeyPressEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import fr.anarchick.skb.screen.KeyReleasedInterface;

@Environment(EnvType.CLIENT)
@Mixin(HandledScreen.class)
public abstract class GUIScreenMixin implements KeyReleasedInterface {

    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        KeyPressEvent.onKeyEvent(keyCode, true);
    }

    // We have to use the interface to be able to override the method
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        KeyPressEvent.onKeyEvent(keyCode, false);
        return false;
    }

}
