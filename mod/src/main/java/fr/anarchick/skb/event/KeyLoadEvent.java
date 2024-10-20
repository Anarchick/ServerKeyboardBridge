package fr.anarchick.skb.event;

import fr.anarchick.skb.ServerKeyboardBridge;
import fr.anarchick.skb.core.KeyEntry;
import fr.anarchick.skb.core.PluginChannels;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public record KeyLoadEvent(boolean shouldReset, Identifier identifier, String name, String description, String category, short keyCode) implements CustomPayload {

    public static final CustomPayload.Id<KeyLoadEvent> CHANNEL = new CustomPayload.Id<>(PluginChannels.LOAD_KEYS.getId());

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return CHANNEL;
    }

    public void load(ClientPlayNetworking.Context context) {
        System.out.println("this = " + this);
    }

    public static void onKeyLoad(MinecraftClient client, PacketByteBuf buf) {
        byte size = buf.readByte();
        byte i = buf.readByte();
        KeyEntry keyEntry = fromBuffer(buf);

        // Read data async and then use client.execute() after for thread safety.
        client.execute(() -> {
            if (i == 0) { // only the first packet will reset the key entries
                ServerKeyboardBridge.clearKeyEntries();
            }

            ServerKeyboardBridge.addKeyEntry(keyEntry);

            if (i == size) { // last packet
                ServerKeyboardBridge.reload();
            }

            client.getToastManager().add(
                    SystemToast.create(client, SystemToast.Type.NARRATOR_TOGGLE,
                            Text.translatable("serverKeyboardBridge.toast.title"),
                            Text.translatable("serverKeyboardBridge.toast.description")
                    )
            );
        });

    }

    private static KeyEntry fromBuffer(PacketByteBuf buf) {
        // must readByte before read String
        buf.readByte();
        Identifier id = buf.readIdentifier();
        buf.readByte();
        String name = buf.readString();
        buf.readByte();
        String description = buf.readString();
        buf.readByte();
        String category = buf.readString();
        short keyCode = buf.readShort();

        return new KeyEntry(id, name, description, category, keyCode);
    }

}
