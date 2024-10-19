package fr.anarchick.skb.mixin;

import fr.anarchick.skb.ServerKeyboardBridge;
import fr.anarchick.skb.core.KeyEntry;
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

import java.util.Optional;

@Environment(EnvType.CLIENT)
@Mixin(KeyBinding.class)
public abstract class KeyBindingMixin {

	// Inject code into the setKeyPressed method before vanilla code
	@Inject(method = "setKeyPressed", at = @At("HEAD"))
	private static void input(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
		Optional<KeyEntry> keyEntry = ServerKeyboardBridge.getKeyEntry(key.getCode());

		// Send key to server
		if (keyEntry.isPresent()) {
			// Client seems to not be able to detect key pressed if the player is in a GUI
			boolean playerIsInGUI = (MinecraftClient.getInstance().currentScreen != null);
			KeyPressEvent.onKeyPress(keyEntry.get().getNamespacedKey(), pressed, playerIsInGUI);
		}

	}

}