package fr.anarchick.skb.core;

import fr.anarchick.skb.ServerKeyboardBridge;
import net.minecraft.util.Identifier;

public enum PluginChannels {

    HANDSHAKE, KEY_EVENT, LOAD_KEYS;

    private final Identifier id;

    PluginChannels() {
        this.id = new Identifier(ServerKeyboardBridge.MOD_ID, name().toLowerCase());
    }

    public Identifier getId() {
        return id;
    }

}
