package fr.anarchick.skb.event;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class KeyReleaseEvent extends KeyEvent {

    public KeyReleaseEvent(Player player, NamespacedKey key, boolean isInGUI) {
        super(player, key, false, isInGUI);
    }

}
