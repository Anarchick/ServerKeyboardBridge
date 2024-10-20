package fr.anarchick.skb.screen;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface KeyReleasedInterface {

    boolean keyReleased(int keyCode, int scanCode, int modifiers);

}
