package fr.anarchick.skb.skript;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import fr.anarchick.skb.ServerKeyboardBridge;
import fr.anarchick.skb.core.KeyEntry;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Name("Register Key Entry")
@Description({
        "Register a key entry with a unique id, a name, a description, a category and a keycode",
        "the id must be unique and is used to identify the key entry",
        "the id must be lowercase and contain only letters, numbers and underscores",
        "keycode can be found here : https://www.glfw.org/docs/latest/group__keys.html"
})
@Examples({
        "on load:",
        "\tregister key entry with id \"horse\" named \"spawn your horse\", category \"page 1\" and keycode 72",

})
@Since("1.1.0")
public class EffRegisterKeyEntry extends Effect {

    private static final String pattern = "register key entry with id %string% named %string%, [description %-string%,] category %string% and keycode %number%";

    static {
        Skript.registerEffect(EffRegisterKeyEntry.class, pattern);
    }

    private Expression<String> exprId;
    private Expression<String> exprName;
    private Expression<String> exprDescription;
    private Expression<String> exprCategory;
    private Expression<Number> exprKeycode;

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expr, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        exprId = (Expression<String>) expr[0];
        exprName = (Expression<String>) expr[1];
        exprDescription = (Expression<String>) expr[2];
        exprCategory = (Expression<String>) expr[3];
        exprKeycode = (Expression<Number>) expr[4];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        String id = exprId.getSingle(event).toLowerCase();
        String name = exprName.getSingle(event);
        String description = (exprDescription == null) ? "" : exprDescription.getSingle(event);
        String category = exprCategory.getSingle(event);
        Number keycodeNumber = exprKeycode.getSingle(event);
        short keycode = keycodeNumber.shortValue();

        NamespacedKey namespacedKey = new NamespacedKey(ServerKeyboardBridge.getInstance(), id);
        KeyEntry keyEntry = new KeyEntry(namespacedKey, name, description, category, keycode);
        ServerKeyboardBridge.getInstance().registerKey(keyEntry);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        if (event == null || exprId == null || exprName == null || exprCategory == null || exprKeycode == null) {
            return pattern;
        }

        return String.format("register key entry with id %s named %s, description %s, category %s and keycode %s",
                exprId.toString(event, debug),
                exprName.toString(event, debug),
                exprDescription.toString(event, debug),
                exprCategory.toString(event, debug),
                exprKeycode.toString(event, debug)
        );
    }

}
