package fr.anarchick.skb.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import fr.anarchick.skb.ServerKeyboardBridge;
import fr.anarchick.skb.event.KeyEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EvtKeyEvent extends SkriptEvent {

    private static final String[] patterns = new String[] {
            "key pressed with id %string%",
            "key release with id %string%"
    };

    static {
        Skript.registerEvent("Key Pressed", EvtKeyEvent.class, KeyEvent.class, patterns)
                .description("Called when the player press his keyboard key or mouse key. ServerKeyboardBridge Mod MUST be installed on the client. You can listen every input with a limit of 20 custom key entries.")
                .examples("on key pressed with id \"horse\":",
                        "\tset {_playerIsInGui} to event-boolean\n",
                        "\tif {_playerIsInGui} is false:\n",
                        "\t\tspawn an horse at event-player")
                .since("1.1.0");
        EventValues.registerEventValue(KeyEvent.class, Player.class, new Getter<>() {
            @Override
            @NotNull
            public Player get(KeyEvent event) {
                return event.getPlayer();
            }
        }, 0);
        EventValues.registerEventValue(KeyEvent.class, Boolean.class, new Getter<>() {
            @Override
            @NotNull
            public Boolean get(KeyEvent event) {
                return event.isInGUI();
            }
        }, 0);
    }

    private int matchedPattern;
    private Literal<String> litId;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.@NotNull ParseResult parseResult) {
        this.matchedPattern = matchedPattern;
        litId = (Literal<String>) args[0];
        return true;
    }

    @Override
    public boolean check(@NotNull Event event) {
        if (event instanceof KeyEvent keyEvent) {

            if (keyEvent.isPressed() != (matchedPattern == 0)) {
                return false;
            }

            String id = litId.getSingle(event);
            NamespacedKey namespacedKey = keyEvent.getKey();
            return namespacedKey.namespace().equalsIgnoreCase(ServerKeyboardBridge.getInstance().getName()) && namespacedKey.getKey().equalsIgnoreCase(id);
        }
        return false;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        String pattern = (matchedPattern == 0) ? patterns[0] : patterns[1];

        if (event == null) {
            return pattern;
        }

        return String.format(pattern.replace("%string%", "%s"), litId.toString(event, debug));
    }
}
