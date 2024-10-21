package fr.anarchick.skb.event;

import fr.anarchick.skb.ServerKeyboardBridge;
import fr.anarchick.skb.core.KeyEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class KeyLoadEvent {

    public static void onKeyLoad(MinecraftClient client, PacketByteBuf buf) {
        byte size = buf.readByte();
        byte i = buf.readByte();
        KeyEntry keyEntry = fromBuffer(buf);

        // Read data async and then use client.execute() after for thread safety.
        client.execute(() -> {

            if (i == 1) { // only the first packet will reset the key entries
                ServerKeyboardBridge.clearKeyEntries();
                client.getToastManager().add(
                        SystemToast.create(client, SystemToast.Type.NARRATOR_TOGGLE,
                                Text.translatable("serverKeyboardBridge.toast.title"),
                                Text.translatable("serverKeyboardBridge.toast.description")
                        )
                );
            }

            ServerKeyboardBridge.addKeyEntry(keyEntry);

            if (i == size) { // last packet
                ServerKeyboardBridge.reload();
            }


        });

    }

    private static KeyEntry fromBuffer(PacketByteBuf buf) {
        Identifier id = buf.readIdentifier();
        String name = buf.readString();
        String description = buf.readString();
        String category = buf.readString();
        short keyCode = buf.readShort();
        return new KeyEntry(id, name, description, category, keyCode);
    }

}
