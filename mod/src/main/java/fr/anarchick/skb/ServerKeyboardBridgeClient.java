package fr.anarchick.skb;

import fr.anarchick.skb.core.PluginChannels;
import fr.anarchick.skb.event.KeyLoadEvent;
import fr.anarchick.skb.event.LoginEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public class ServerKeyboardBridgeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> LoginEvent.onLogout()));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> LoginEvent.onLogin());

        // Receive keybindings data from the server
        ClientPlayNetworking.registerGlobalReceiver(PluginChannels.LOAD_KEYS.getId(),
                (client, handler, buf, responseSender) -> KeyLoadEvent.onKeyLoad(client, buf));

        ServerKeyboardBridge.LOGGER.info("ServerKeyboardBridgeClient initialized");
    }

}
