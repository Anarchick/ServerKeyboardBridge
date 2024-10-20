package fr.anarchick.skb;

import fr.anarchick.skb.core.KeyEntry;
import fr.anarchick.skb.core.KeyEntryIO;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Unmodifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;

public class ServerKeyboardBridge implements ModInitializer {
	public static final String VERSION = "1.0.0";
	public static final String MOD_ID = "serverkeyboardbridge";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static final HashMap<Identifier, KeyEntry> KEY_ENTRIES = new HashMap<>();
	private static final HashMap<Integer, KeyEntry> KEYCODE_ENTRIES = new HashMap<>();

	@Override
	public void onInitialize() {

		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
	}

	public static void addKeyEntry(KeyEntry keyEntry) {
		KEY_ENTRIES.put(keyEntry.getNamespacedKey(), keyEntry);
	}

	@Unmodifiable
	public static HashMap<Identifier, KeyEntry> getKeyEntries() {
		return new HashMap<>(KEY_ENTRIES);
	}

	public static Optional<KeyEntry> getKeyEntry(Identifier id) {
		return Optional.ofNullable(KEY_ENTRIES.get(id));
	}

	public static Optional<KeyEntry> getKeyEntry(int keyCode) {
		return Optional.ofNullable(KEYCODE_ENTRIES.get(keyCode));
	}

	public static void clearKeyEntries() {
		KEY_ENTRIES.clear();
		KEYCODE_ENTRIES.clear();
	}

	public static void reloadMapping() {
		KEYCODE_ENTRIES.clear();
		KEY_ENTRIES.forEach((id, keyEntry) -> KEYCODE_ENTRIES.put(keyEntry.getKeyCode(), keyEntry));
	}

	public static void reload() {
		KeyEntryIO.loadConfig();
		KeyEntryIO.saveConfig(); // Save new inputs
		reloadMapping();
	}

}