package fr.anarchick.skb.core;

import fr.anarchick.skb.ServerKeyboardBridge;

public enum PluginChannels {

    HANDSHAKE, KEY_EVENT, LOAD_KEYS;

    private final String id;

    PluginChannels() {
        this.id = String.format("%s:%s", ServerKeyboardBridge.getInstance().getName(), name()).toLowerCase();;
    }

    public String getId() {
        return id;
    }

}
