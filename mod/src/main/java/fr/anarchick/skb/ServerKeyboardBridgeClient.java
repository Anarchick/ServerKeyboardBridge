package fr.anarchick.skb;

import fr.anarchick.skb.event.KeyLoadEvent;
import fr.anarchick.skb.event.KeyPressEvent;
import fr.anarchick.skb.event.LoginEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

@Environment(EnvType.CLIENT)
public class ServerKeyboardBridgeClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> LoginEvent.onLogout()));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> LoginEvent.onLogin());


        PayloadTypeRegistry.playC2S().register(LoginEvent.CHANNEL, LoginEvent.CODEC);
        PayloadTypeRegistry.playC2S().register(KeyPressEvent.CHANNEL, KeyPressEvent.CODEC);

        PayloadTypeRegistry.playS2C().register(KeyLoadEvent.CHANNEL, KeyLoadEvent.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(KeyLoadEvent.CHANNEL,
                (payload, context) -> payload.load());

        ServerKeyboardBridge.LOGGER.info("ServerKeyboardBridgeClient initialized");
    }

}
