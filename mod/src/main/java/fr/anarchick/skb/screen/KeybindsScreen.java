package fr.anarchick.skb.screen;

import fr.anarchick.skb.ServerKeyboardBridge;
import fr.anarchick.skb.core.KeyEntry;
import fr.anarchick.skb.core.KeyEntryIO;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Code from the {@link net.minecraft.client.gui.screen.option.KeybindsScreen} class.
 */
@Environment(EnvType.CLIENT)
public class KeybindsScreen extends Screen {

    public static final Text TITLE = Text.translatable("serverKeyboardBridge.config.title").formatted(Formatting.BOLD);

    private final Screen parent;
    private final int categoryId;
    private final Text category;
    @Nullable
    public KeyEntry selectedKeyBinding;
    private int y;

    @NotNull
    private static Text getCategory(int categoryId) {
        List<String> categories = ServerKeyboardBridge.getKeyEntries().values().stream()
                .map(KeyEntry::getCategory)
                .distinct()
                .toList();
        int size = categories.size();
        String category = categories.get((categoryId % size + size) % size);
        return Text.translatableWithFallback(category, category);
    }

    @NotNull
    @Unmodifiable
    public static List<KeyEntry> fromCategory(@Nullable String category) {
        if (category == null) {
            String firstCategory = ServerKeyboardBridge.getKeyEntries().values().stream()
                    .map(KeyEntry::getCategory)
                    .findFirst().orElse("");
            return fromCategory(firstCategory);
        }

        return ServerKeyboardBridge.getKeyEntries().values().stream()
                .filter(keyEntry -> keyEntry.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    public KeybindsScreen(@Nullable Screen parent, int categoryId) {
        super(TITLE);
        this.parent = parent;
        this.categoryId = categoryId;
        this.category = getCategory(categoryId);
    }

    public void update() {
        close();
        assert this.client != null;
        this.client.setScreen(new KeybindsScreen(this.parent, this.categoryId));
    }

    @Override
    public void close() {
        KeyEntryIO.saveConfig();
        assert this.client != null;
        this.client.setScreen(this.parent);
    }

    @Override
    protected void init() {
        initNavigator();
        initKeybindings();
    }

    private void initNavigator() {
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().margin(4, 4, 4, 0);
        GridWidget.Adder adder = gridWidget.createAdder(3);

        adder.add(this.createCategoryButton(categoryId - 1));
        adder.add(new TextWidget(textRenderer.getWidth(this.category.asOrderedText()), 22, this.category, this.textRenderer));
        adder.add(this.createCategoryButton(categoryId + 1));

        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, 0, this.width, this.height, 0.5F, 0.1F);
        gridWidget.forEachChild(this::addDrawableChild);
        this.y = gridWidget.getY() + gridWidget.getHeight();
    }

    private void initKeybindings() {
        GridWidget gridWidget = new GridWidget();
        gridWidget.getMainPositioner().margin(4, 4, 4, 0);
        GridWidget.Adder adder = gridWidget.createAdder(3);

        for (KeyEntry keyEntry : fromCategory(this.category.getString())) {
            Text name = keyEntry.getName();

            adder.add(new TextWidget(168, 22, name, this.textRenderer)
                    .alignLeft()
            );
            adder.add(this.createKeyButton(keyEntry));
            adder.add(this.createResetButton(keyEntry));
        }

        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, this.y + 10, this.width, this.height, 0.5F, 0.0F);
        gridWidget.forEachChild(this::addDrawableChild);
    }

    public Widget createCategoryButton(int categoryId) {
        if (getCategory(categoryId).equals(this.category)) {
            return EmptyWidget.ofWidth(98);
        } else {
            return ButtonWidget.builder(
                    getCategory(categoryId),
                    button -> {
                        assert this.client != null;
                        this.client.setScreen(new KeybindsScreen(this.parent, categoryId));
                    }
            ).width(98).build();
        }
    }

    public ButtonWidget createKeyButton(KeyEntry keyEntry) {
        int keycode = keyEntry.getKeyCode();
        MutableText text = Text.translatable(InputUtil.Type.KEYSYM.createFromCode(keycode).getTranslationKey());
        @Nullable Text description = keyEntry.getDescription();

        // Fix for the key translation
        // I don't know how Minecraft do that in his own keybindings screen
        if (text.getString().startsWith("key.keyboard.")) {
            text = Text.literal(text.getString().replace("key.keyboard.", "").toUpperCase());
        }

        if (!keyEntry.isUnique() && keycode != InputUtil.UNKNOWN_KEY.getCode()) {
            text = text.formatted(Formatting.RED);
        }

        ButtonWidget buttonWidget = ButtonWidget.builder(
                text,
                button -> {
                    this.selectedKeyBinding = keyEntry;
                    Text editTitle = Text.literal(String.format("> %s <", button.getMessage().getString()))
                            .formatted(Formatting.YELLOW);
                    button.setMessage(editTitle);
                }
        ).width(60).build();

        if (description != null) {
            buttonWidget.setTooltip(Tooltip.of(description, description));
        }

        return buttonWidget;
    }

    public ButtonWidget createResetButton(KeyEntry keyEntry) {
        Text name = Text.literal("Reset");
        ButtonWidget buttonWidget = ButtonWidget.builder(
                name,
                button -> {
                    keyEntry.resetKeyCode();
                    update();
                }
        ).width(40).build();
        buttonWidget.active = !keyEntry.isDefault();
        return buttonWidget;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.selectedKeyBinding != null) {
            this.selectedKeyBinding.setKeyCode(InputUtil.Type.MOUSE.createFromCode(button).getCode());
            this.selectedKeyBinding = null;
            update();
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.selectedKeyBinding != null) {

            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                this.selectedKeyBinding.setKeyCode(InputUtil.UNKNOWN_KEY.getCode());
            } else {
                this.selectedKeyBinding.setKeyCode(InputUtil.fromKeyCode(keyCode, scanCode).getCode());
            }

            this.selectedKeyBinding = null;
            update();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        //this.renderBackground(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 16777215);
        super.render(context, mouseX, mouseY, delta);
    }

}
