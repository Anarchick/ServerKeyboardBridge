package fr.anarchick.skb.event;

import fr.anarchick.skb.ServerKeyboardBridge;
import fr.anarchick.skb.core.KeyEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;

import java.util.ArrayList;

public class KeyLoadEvent {

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
