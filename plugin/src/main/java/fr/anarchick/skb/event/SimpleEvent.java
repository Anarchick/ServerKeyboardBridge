package fr.anarchick.skb.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;

    public SimpleEvent(@NotNull final Player player, boolean isAsync) {
        super(isAsync);
        this.player = player;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }

}
