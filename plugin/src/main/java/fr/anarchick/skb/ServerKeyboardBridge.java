package fr.anarchick.skb;

import fr.anarchick.skb.core.KeyEntry;
import fr.anarchick.skb.core.PluginChannels;
import fr.anarchick.skb.core.PluginMessageListeners;
import fr.anarchick.skb.event.SkbJoinEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public final class ServerKeyboardBridge extends JavaPlugin implements org.bukkit.event.Listener {

    public static final String PROTOCOL_VERSION = "1."; // The client Major version must be the same as this
    private static java.util.logging.@NotNull Logger LOGGER = Bukkit.getLogger();
    private static ServerKeyboardBridge INSTANCE = null;
    private static final int KEY_ENTRIES_LIMIT = 20;
    private static final @ApiStatus.Internal HashSet<KeyEntry> KEY_ENTRIES = new HashSet<>();
    private static final HashMap<Player, String> BRIDGE_PLAYERS = new HashMap<>();
    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final PluginMessageListeners pluginMessageListeners = new PluginMessageListeners();
    private boolean kickPlayerIfNoMod;
    private Component kickMessage;
    private boolean sendMessageIfNoMod;
    private Component messageIfNoMod;

    public static ServerKeyboardBridge getInstance() {
        return INSTANCE;
    }

    // Use iterator for security, this prevents the set from being modified and larger than limit
    public static Iterator<KeyEntry> getKeyEntries() {
        return KEY_ENTRIES.iterator();
    }

    public static int getKeyEntriesSize() {
        return KEY_ENTRIES.size();
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        LOGGER = getLogger();
        Messenger messenger = Bukkit.getMessenger();
        messenger.registerIncomingPluginChannel(this, PluginChannels.HANDSHAKE.getId(), pluginMessageListeners);
        messenger.registerOutgoingPluginChannel(this, PluginChannels.LOAD_KEYS.getId());
        messenger.registerIncomingPluginChannel(this, PluginChannels.KEY_EVENT.getId(), pluginMessageListeners);

        getServer().getPluginManager().registerEvents(this, this);

        //test();

        saveDefaultConfig();
        this.kickPlayerIfNoMod = getConfig().getBoolean("ifNoModDetected.kickPlayer", false);
        this.kickMessage = MINI_MESSAGE.deserialize(getConfig().getString("ifNoModDetected.kickMessage", "<red>You need to have the mod <white>ServerKeyboardBridge <red>installed to join this server!"));
        this.sendMessageIfNoMod = getConfig().getBoolean("ifNoModDetected.sendMessage", false);
        this.messageIfNoMod = MINI_MESSAGE.deserialize(getConfig().getString("ifNoModDetected.message", "<green>You can have a better game experience if you install the mod <white>ServerKeyboardBridge"));

        Metrics metrics = new Metrics(this, 23694);
        LOGGER.info("ServerKeyboardBridge enabled");
    }

    @Override
    public void onDisable() {
        Messenger messenger = Bukkit.getMessenger();
        messenger.unregisterIncomingPluginChannel(this);
        messenger.unregisterOutgoingPluginChannel(this);
    }

    public static void info(String message) {
        LOGGER.info(message);
    }

    public boolean registerKey(KeyEntry keyEntry) {
        if (!KEY_ENTRIES.contains(keyEntry) && KEY_ENTRIES.size() >= KEY_ENTRIES_LIMIT) {
            LOGGER.warning("Key entries limit reached. Can't add : " + keyEntry);
            return false;
        }
        return KEY_ENTRIES.add(keyEntry);
    }

    @ApiStatus.Internal
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            Player player = event.getPlayer();

            if (BRIDGE_PLAYERS.get(player) == null) {
                if (kickPlayerIfNoMod && !player.hasPermission("serverkeyboardbridge.bypass.kickPlayerIfNoMod")) {
                    player.kick(kickMessage);
                    return;
                }

                if (sendMessageIfNoMod && !player.hasPermission("serverkeyboardbridge.bypass.messageIfNoMod")) {
                    player.sendMessage(messageIfNoMod);
                }
            }

        }, 20);
    }

    @ApiStatus.Internal
    @EventHandler
    public void skbJoinEvent(SkbJoinEvent event) {
        BRIDGE_PLAYERS.put(event.getPlayer(), event.getVersion());
    }

    @ApiStatus.Internal
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        BRIDGE_PLAYERS.remove(event.getPlayer());
    }

    /**
     * Check if the player has installed the mod and get the client version.
     * Value are lost after a reload.
     * @param player the player
     * @return the client version or an empty string if the player has not installed the mod
     */
    @NotNull
    public String getClientVersion(@NotNull Player player) {
        return BRIDGE_PLAYERS.getOrDefault(player, "");
    }

    @ApiStatus.Internal
    private void test() {
        KeyEntry keyEntry = new KeyEntry(this, "test", "name1", "description", "minecraft default", (short) 84);
        KeyEntry keyEntry2 = new KeyEntry(this, "test2", "test2", "", "minecraft default minecraft default", (short) 84);
        registerKey(keyEntry);
        registerKey(keyEntry2);
    }

    /*
    @EventHandler
    public void keyPressed(KeyEvent event) {
        Player player = event.getPlayer();
        NamespacedKey namespacedKey = event.getKey();
        boolean isPressed = event.isPressed();
        boolean isInGui = event.isInGUI();
        System.out.println("player = " + player);
        System.out.println("namespacedKey = " + namespacedKey.asString());
        System.out.println("isPressed = " + isPressed);
        System.out.println("isInGui = " + isInGui);
    }

     */

}
