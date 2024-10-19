package fr.anarchick.skb.core;

import fr.anarchick.skb.ServerKeyboardBridge;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;


public class KeyEntry {

    public static KeyEntry fromBuffer(PacketByteBuf buf) {
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

    private final Identifier namespacedKey;
    private final Text name;
    private final @Nullable Text description;
    private final String category;
    private int keyCode;
    private final short keyCodeDefault;

    public KeyEntry(Identifier namespacedKey, String name, String description, String category, short keyCode) {
        this.namespacedKey = namespacedKey;

        this.name = Text.translatableWithFallback(name, name);
        this.description = (description.isBlank()) ? null : Text.translatableWithFallback(description, description);
        this.category = category;
        this.keyCode = keyCode;
        this.keyCodeDefault = keyCode;
    }

    public Identifier getNamespacedKey() {
        return namespacedKey;
    }

    public Text getName() {
        return name;
    }

    @Nullable
    public Text getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public void resetKeyCode() {
        setKeyCode(keyCodeDefault);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        KeyEntry keyEntry = (KeyEntry) obj;
        return this.namespacedKey.equals(keyEntry.namespacedKey);
    }

    @Override
    public int hashCode() {
        return namespacedKey.hashCode();
    }

    @Override
    public String toString() {
        return String.format("KeyEntry{%s;%s;%s;%s;%d}", namespacedKey.toString(), name, description, category, keyCode);
    }

    public boolean isDefault() {
        return keyCode == keyCodeDefault;
    }

    /**
     * Check if the key is already used by another key entry
     * @return true if the key is not used
     */
    public boolean isUnique() {
        for (KeyEntry keyEntry : ServerKeyboardBridge.getKeyEntries().values()) {
            if (keyEntry.getKeyCode() == keyCode && !keyEntry.equals(this)) {
                return false;
            }
        }

        return true;
    }
}

