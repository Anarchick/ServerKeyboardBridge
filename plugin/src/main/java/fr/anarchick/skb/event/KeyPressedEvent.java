package fr.anarchick.skb.event;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class KeyPressedEvent extends KeyEvent {

    public KeyPressedEvent(Player player, NamespacedKey key, boolean isInGUI) {
        super(player, key, true, isInGUI);
    }

}
