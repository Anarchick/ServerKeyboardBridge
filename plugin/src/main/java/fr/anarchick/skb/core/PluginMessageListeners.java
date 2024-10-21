package fr.anarchick.skb.core;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.anarchick.skb.ServerKeyboardBridge;
import fr.anarchick.skb.event.SkbJoinEvent;
import fr.anarchick.skb.event.KeyEvent;
import fr.anarchick.skb.event.KeyPressedEvent;
import fr.anarchick.skb.event.KeyReleaseEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class PluginMessageListeners implements PluginMessageListener {

    private static final Component MISMATCH_MESSAGE = ServerKeyboardBridge.MINI_MESSAGE.deserialize("<red>The server key bridge version is not compatible with the client version. Please use version : <white>" + ServerKeyboardBridge.PROTOCOL_VERSION + "x.x");

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
        if (channel.equalsIgnoreCase(PluginChannels.KEY_EVENT.getId())) {
            decodeKeyEvent(player, message);
        } else if (channel.equalsIgnoreCase(PluginChannels.HANDSHAKE.getId())) {
            sendGreeting(player, message);
        }
    }

    private void decodeKeyEvent(Player player, byte[] message) {
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        boolean isPressed = in.readBoolean();
        boolean isInGUI = in.readBoolean();
        in.readByte();
        String identifier = in.readLine();
        assert identifier != null;
        String[] split = identifier.split(":");

        if (split.length != 2) {
            throw new IllegalArgumentException("Invalid identifier format: " + identifier);
        }

        NamespacedKey namespacedKey = new NamespacedKey(split[0], split[1]);
        KeyEvent event = (isPressed) ? new KeyPressedEvent(player, namespacedKey, isInGUI) : new KeyReleaseEvent(player, namespacedKey, isInGUI);

        Bukkit.getScheduler().runTask(ServerKeyboardBridge.getInstance(), event::callEvent);
    }

    private void sendGreeting(Player player, byte[] message) {
        if (!ServerKeyboardBridge.getInstance().getClientVersion(player).isEmpty()) {
            // Player can only send handshake once
            // This is to prevent client trying to overflow the server
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        in.readByte();
        String clientVersion = in.readLine();
        ServerKeyboardBridge.info("Received handshake from " + player.getName() + " with version " + clientVersion);
        new SkbJoinEvent(player, clientVersion).callEvent();

        if (clientVersion == null || !clientVersion.startsWith(ServerKeyboardBridge.PROTOCOL_VERSION)) {
            ServerKeyboardBridge.info("Client version mismatch : " + clientVersion);
            player.sendMessage(MISMATCH_MESSAGE);
            return;

        }

        Bukkit.getScheduler().runTaskLater(ServerKeyboardBridge.getInstance(), () -> {
            byte size = (byte) ServerKeyboardBridge.getKeyEntriesSize();
            byte i = 0;

            for (Iterator<KeyEntry> it = ServerKeyboardBridge.getKeyEntries(); it.hasNext(); ) {
                KeyEntry keyEntry = it.next();
                i++;

                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeByte(size);
                out.writeByte(i);
                out.writeUTF(keyEntry.namespacedKey().asString());
                out.writeUTF(keyEntry.name());
                out.writeUTF(keyEntry.description());
                out.writeUTF(keyEntry.category());
                out.writeShort(keyEntry.keyCode());

                player.sendPluginMessage(ServerKeyboardBridge.getInstance(), PluginChannels.LOAD_KEYS.getId(), out.toByteArray());
            }

        }, 20L);
    }

}
