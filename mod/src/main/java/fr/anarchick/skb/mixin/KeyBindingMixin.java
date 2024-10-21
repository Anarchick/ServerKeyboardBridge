package fr.anarchick.skb.mixin;

import fr.anarchick.skb.event.KeyPressEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {

	private static final MinecraftClient CLIENT = MinecraftClient.getInstance();

	@Inject(method = "setKeyPressed", at = @At("HEAD"))
	private static void input(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
		// On mc 1.20.4+ this method is called even if the client is in a screen
		// This method is called often so we can't use instanceof HandledScreen here
		// It's better to use GUIScreenMixin instead
		if (CLIENT.currentScreen == null) {
			KeyPressEvent.onKeyEvent(key.getCode(), pressed);
		}
	}

}