package fr.anarchick.skb.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fr.anarchick.skb.ServerKeyboardBridge;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import screen.KeybindsScreen;

@Environment(EnvType.CLIENT)
@Mixin(GameMenuScreen.class)
public abstract class MenuScreen extends Screen {

    @Unique
    private static final Text SERVER_KEY_BIND_BUTTON_TITLE = Text.translatable("serverKeyboardBridge.button").formatted(Formatting.AQUA, Formatting.BOLD);
    @Unique
    private static final Text TOOLTIP = Text.translatable("serverKeyboardBridge.button.tooltip").formatted(Formatting.GRAY);

    protected MenuScreen(Text title) {
        super(title);
    }

    @Redirect(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget$Adder;add(Lnet/minecraft/client/gui/widget/Widget;ILnet/minecraft/client/gui/widget/Positioner;)Lnet/minecraft/client/gui/widget/Widget;"))
    private <T extends Widget> T add(GridWidget.Adder adder, T widget, int occupiedColumns, Positioner positioner, @Local GridWidget gridWidget) {
        if (!ServerKeyboardBridge.getKeyEntries().isEmpty()) {
            adder.add(ButtonWidget.builder(
                    SERVER_KEY_BIND_BUTTON_TITLE,
                    button -> {
                        assert this.client != null;
                        this.client.setScreen(new KeybindsScreen(this, 0));
                    }
            ).tooltip(Tooltip.of(TOOLTIP, TOOLTIP)).width(204).build(), 2, gridWidget.copyPositioner().marginTop(50));
        }

        adder.add(widget, 2);

        return widget;
    }

}
