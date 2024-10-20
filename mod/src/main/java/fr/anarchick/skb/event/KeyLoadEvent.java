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
        ArrayList<KeyEntry> keyEntries = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            KeyEntry keyEntry = KeyEntry.fromBuffer(buf);
            keyEntries.add(keyEntry);
        }


        // Read data async and then use client.execute() after for thread safety.
        client.execute(() -> {
            ServerKeyboardBridge.clearKeyEntries();

            for (KeyEntry keyEntry : keyEntries) {
                ServerKeyboardBridge.addKeyEntry(keyEntry);
            }

            ServerKeyboardBridge.reload();

            client.getToastManager().add(
                    SystemToast.create(client, SystemToast.Type.NARRATOR_TOGGLE,
                            Text.translatable("serverKeyboardBridge.toast.title"),
                            Text.translatable("serverKeyboardBridge.toast.description")
                    )
            );
        });

    }

}
