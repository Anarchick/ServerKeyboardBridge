package fr.anarchick.skb.event;

import fr.anarchick.skb.core.PluginChannels;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class KeyPressEvent {

    public static void onKeyPress(Identifier id, boolean isPressed, boolean playerInGUI) {
        PacketByteBuf out = PacketByteBufs.create();
        out.writeBoolean(isPressed);
        out.writeBoolean(playerInGUI);
        out.writeIdentifier(id);
        ClientPlayNetworking.send(PluginChannels.KEY_EVENT.getId(), out);
    }

}
