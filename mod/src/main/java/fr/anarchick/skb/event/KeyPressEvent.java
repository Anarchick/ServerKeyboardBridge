package fr.anarchick.skb.event;

import fr.anarchick.skb.ServerKeyboardBridge;
import fr.anarchick.skb.core.KeyEntry;
import fr.anarchick.skb.core.PluginChannels;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;

import java.util.Optional;

public class KeyPressEvent {

    public static void onKeyEvent(int code, boolean isPressed) {
        Optional<KeyEntry> keyEntry = ServerKeyboardBridge.getKeyEntry(code);

        // Send key to server
        if (keyEntry.isPresent()) {
            boolean playerIsInGUI = (MinecraftClient.getInstance().currentScreen != null);
            PacketByteBuf out = PacketByteBufs.create();
            out.writeBoolean(isPressed);
            out.writeBoolean(playerIsInGUI);
            out.writeIdentifier(keyEntry.get().getNamespacedKey());
            ClientPlayNetworking.send(PluginChannels.KEY_EVENT.getId(), out);
        }
    }

}
