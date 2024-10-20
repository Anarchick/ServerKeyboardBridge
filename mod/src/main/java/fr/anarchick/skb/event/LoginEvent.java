package fr.anarchick.skb.event;

import fr.anarchick.skb.ServerKeyboardBridge;
import fr.anarchick.skb.core.KeyEntryIO;
import fr.anarchick.skb.core.PluginChannels;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record LoginEvent(String version) implements CustomPayload {

    private static final Id<LoginEvent> CHANNEL = new CustomPayload.Id<>(PluginChannels.HANDSHAKE.getId());

    @Override
    public Id<? extends CustomPayload> getId() {
        return CHANNEL;
    }

    public static void onLogin() {
        ClientPlayNetworking.send(new LoginEvent(ServerKeyboardBridge.VERSION));
        ServerKeyboardBridge.LOGGER.info("Sent handshake");
    }

    public static void onLogout() {
        KeyEntryIO.saveConfig();
        ServerKeyboardBridge.clearKeyEntries();
        ServerKeyboardBridge.LOGGER.info("Cleared key entries");
    }

}
