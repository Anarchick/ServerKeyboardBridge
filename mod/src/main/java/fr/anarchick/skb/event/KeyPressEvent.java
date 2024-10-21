package fr.anarchick.skb.event;

import fr.anarchick.skb.ServerKeyboardBridge;
import fr.anarchick.skb.core.KeyEntry;
import fr.anarchick.skb.core.PluginChannels;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Internal
public record KeyPressEvent(boolean isPressed, boolean playerIsInGUI, Identifier identifier) implements CustomPayload {

    public static final CustomPayload.Id<KeyPressEvent> CHANNEL = new CustomPayload.Id<>(PluginChannels.KEY_EVENT.getId());
    public static final PacketCodec<RegistryByteBuf, KeyPressEvent> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOL, KeyPressEvent::isPressed,
            PacketCodecs.BOOL, KeyPressEvent::playerIsInGUI,
            Identifier.PACKET_CODEC, KeyPressEvent::identifier,
            KeyPressEvent::new
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return CHANNEL;
    }

    public static void onKeyEvent(int code, boolean isPressed) {
        Optional<KeyEntry> keyEntry = ServerKeyboardBridge.getKeyEntry(code);

        // Send key to server
        if (keyEntry.isPresent()) {
            boolean playerIsInGUI = (MinecraftClient.getInstance().currentScreen != null);
            KeyPressEvent payLoad = new KeyPressEvent(isPressed, playerIsInGUI, keyEntry.get().getNamespacedKey());
            ClientPlayNetworking.send(payLoad);
        }
    }

}
