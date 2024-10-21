package fr.anarchick.skb.core;

import fr.anarchick.skb.ServerKeyboardBridge;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * Must be registered with {@link ServerKeyboardBridge#registerKey(KeyEntry)}.
 *
 * @param namespacedKey the namespaced key
 * @param name        the name of the action to bind the key
 * @param description the description of the key shown as tooltip
 * @param category    in which category should appear
 * @param keyCode     the default key code. See <a href="https://www.glfw.org/docs/latest/group__keys.html">...</a>
 */
public record KeyEntry(@NotNull NamespacedKey namespacedKey, @NotNull String name, @NotNull String description, @NotNull String category, short keyCode) {
    public KeyEntry {
        if (name.isBlank()) throw new IllegalArgumentException("name cannot be empty");
        if (category.isBlank()) throw new IllegalArgumentException("category cannot be empty");
    }

    /**
     * Must be registered with {@link ServerKeyboardBridge#registerKey(KeyEntry)}.
     *
     * @param plugin      the plugin used to generate the NamespacedKey
     * @param id          the id of the key used to generate the NamespacedKey
     * @param name        the name of the action to bind the key
     * @param description the description of the key shown as tooltip
     * @param category    in which category should appear
     * @param keyCode     the default key code. See <a href="https://www.glfw.org/docs/latest/group__keys.html">...</a>
     */
    public KeyEntry(@NotNull Plugin plugin, @NotNull String id, @NotNull String name, @NotNull String description, @NotNull String category, short keyCode) {
        this(new NamespacedKey(plugin, id), name, description, category, keyCode);
    }

    /**
     * only verify the equality of the namespaced key
     * other fields are not taken into account
     * @param obj   the reference object with which to compare.
     * @return true if the namespaced key is equal
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        KeyEntry keyEntry = (KeyEntry) obj;
        return this.namespacedKey.equals(keyEntry.namespacedKey);
    }

    /**
     * only hash the namespaced key
     * other fields are not taken into account
     * @return a hash code value of the namespaceKey.
     */
    @Override
    public int hashCode() {
        return namespacedKey.hashCode();
    }

}
