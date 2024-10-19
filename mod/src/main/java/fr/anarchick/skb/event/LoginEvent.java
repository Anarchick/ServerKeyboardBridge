package fr.anarchick.skb.event;

import fr.anarchick.skb.ServerKeyboardBridge;
import fr.anarchick.skb.core.KeyEntryIO;
import fr.anarchick.skb.core.PluginChannels;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

public class LoginEvent {

    public static void onLogin() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(ServerKeyboardBridge.VERSION);
        ClientPlayNetworking.send(PluginChannels.HANDSHAKE.getId(), buf);
        ServerKeyboardBridge.LOGGER.info("Sent handshake");
    }

    public static void onLogout() {
        KeyEntryIO.saveConfig();
        ServerKeyboardBridge.clearKeyEntries();
        ServerKeyboardBridge.LOGGER.info("Cleared key entries");
    }

}
