package fr.anarchick.skb.core;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import fr.anarchick.skb.ServerKeyboardBridge;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.regex.Pattern;

public class KeyEntryIO {

    private static final File KEYBIND_FILE = new File(MinecraftClient.getInstance().runDirectory, "ServerKeyboardBridge.txt");
    private static final Pattern PATTERN = Pattern.compile("^(\\w+:\\w+)=(\\d+)$");

    private static void createFileIfNotExists() {

        if (!KEYBIND_FILE.exists()) {
            try {
                if (!KEYBIND_FILE.createNewFile()) {
                    throw new NoSuchFileException("Failed to create keybinding file");
                }
            } catch (Exception e) {
                ServerKeyboardBridge.LOGGER.error("Failed to create keybinding file", e);
            }
        }
    }

    public static void loadConfig() {
        createFileIfNotExists();
        ServerKeyboardBridge.LOGGER.info("Loading keybinding file");

        try (BufferedReader bufferedReader = Files.newReader(KEYBIND_FILE, Charsets.UTF_8)) {
            bufferedReader.lines().forEach((line) -> {

                if (PATTERN.matcher(line).matches()) {
                    String[] split = line.split("=");
                    Identifier id = Identifier.of(split[0]);
                    short keyCode = Short.parseShort(split[1]);

                    ServerKeyboardBridge.getKeyEntry(id).ifPresent(keyEntry -> keyEntry.setKeyCode(keyCode));
                } else {
                    ServerKeyboardBridge.LOGGER.warn("Skipping bad format : {}", line);
                }

            });
        } catch (Throwable throwable) {
            ServerKeyboardBridge.LOGGER.error("Failed to load keybinding file", throwable);
        }

    }

    public static void saveConfig() {
        createFileIfNotExists();
        ServerKeyboardBridge.LOGGER.info("Saving keybinding file");

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(KEYBIND_FILE), StandardCharsets.UTF_8))) {
            ServerKeyboardBridge.getKeyEntries().forEach((id, keyEntry) -> writer.println(id.toString() + "=" + keyEntry.getKeyCode()));
        } catch (Throwable e) {
            ServerKeyboardBridge.LOGGER.error("Failed to write keybinding file", e);
        }

    }

}
