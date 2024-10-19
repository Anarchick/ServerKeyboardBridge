package fr.anarchick.skb.event;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkbJoinEvent extends SimpleEvent {

    private final String version;

    public SkbJoinEvent(@NotNull Player player, String version) {
        super(player, false);
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

}
