package fr.anarchick.skb.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.util.SimpleEvent;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import fr.anarchick.skb.event.SkbJoinEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EvtSkbJoin {

    private static final String pattern = "(ServerKeyboardBridge|SKB) player (login|logging in|join[ing])";

    static {
        Skript.registerEvent("ServerKeyboardBridge Join", SimpleEvent.class, SkbJoinEvent.class, pattern)
                .description("Called when the player joins the server with the ServerKeyboardBridge Mod installed. You can get the client mod version with event-string.")
                .examples("on SKB player join:",
                        "	message \"client version : %event-string%\"")
                .since("1.1.0");
        EventValues.registerEventValue(SkbJoinEvent.class, Player.class, new Getter<>() {
            @Override
            @NotNull
            public Player get(SkbJoinEvent event) {
                return event.getPlayer();
            }
        }, 0);
        EventValues.registerEventValue(SkbJoinEvent.class, String.class, new Getter<>() {
            @Override
            @NotNull
            public String get(SkbJoinEvent event) {
                return event.getVersion();
            }
        }, 0);
    }

}
