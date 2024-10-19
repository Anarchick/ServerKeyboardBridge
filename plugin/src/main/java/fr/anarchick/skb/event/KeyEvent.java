package fr.anarchick.skb.event;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class KeyEvent extends SimpleEvent {

    private final NamespacedKey key;
    private final boolean isInGUI;
    private final boolean isPressed;

    public KeyEvent(@NotNull final Player player, @NotNull final NamespacedKey key, boolean isPressed, boolean isInGUI) {
        super(player, false);
        this.key = key;
        this.isPressed = isPressed;
        this.isInGUI = isInGUI;
    }

    @NotNull
    public NamespacedKey getKey() {
        return key;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public boolean isInGUI() {
        return isInGUI;
    }
}
