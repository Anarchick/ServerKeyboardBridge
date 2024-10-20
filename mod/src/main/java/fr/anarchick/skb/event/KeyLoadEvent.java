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
