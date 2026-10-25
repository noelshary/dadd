package de.stylestudio;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.function.Supplier;

public class ConfigScreen extends Screen {
	private static final int COL_W = 140;
	private static final int GAP = 6;
	private static final int ROW = 24;
	private static final int TOP = 44;

	private final Screen parent;
	private final StylesConfig cfg = StylesConfig.get();

	private int colChatX;
	private int colHudX;
	private int colMenuX;
	private int prefixLabelY;

	public ConfigScreen(Screen parent) {
		super(Text.literal("Style Studio"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int total = 3 * COL_W + 2 * GAP;
		colChatX = width / 2 - total / 2;
		colHudX = colChatX + COL_W + GAP;
		colMenuX = colHudX + COL_W + GAP;

		// ---- Chat ----
		int y = TOP;
		cycle(colChatX, y, () -> "Chat-Styles: " + onOff(cfg.chatEnabled), () -> cfg.chatEnabled = !cfg.chatEnabled);
		y += ROW;
		cycle(colChatX, y, () -> "Uhrzeit: " + onOff(cfg.chatTimestamp), () -> cfg.chatTimestamp = !cfg.chatTimestamp);
		y += ROW;
		cycle(colChatX, y, () -> "Uhrzeit-Farbe: " + StylesConfig.colorName(cfg.chatTimestampColor),
			() -> cfg.chatTimestampColor = StylesConfig.nextColor(cfg.chatTimestampColor, false));
		y += ROW;

		prefixLabelY = y + 2;
		y += 12;
		TextFieldWidget prefixField = new TextFieldWidget(textRenderer, colChatX, y, COL_W, 20, Text.literal("Präfix"));
		prefixField.setMaxLength(24);
		prefixField.setText(cfg.chatPrefix == null ? "" : cfg.chatPrefix);
		prefixField.setChangedListener(value -> cfg.chatPrefix = value);
		addDrawableChild(prefixField);
		y += ROW;

		cycle(colChatX, y, () -> "Präfix-Farbe: " + StylesConfig.colorName(cfg.chatPrefixColor),
			() -> cfg.chatPrefixColor = StylesConfig.nextColor(cfg.chatPrefixColor, false));
		y += ROW;
		cycle(colChatX, y, () -> "Textfarbe: " + StylesConfig.colorName(cfg.chatTextColor),
			() -> cfg.chatTextColor = StylesConfig.nextColor(cfg.chatTextColor, true));

		// ---- HUD ----
		y = TOP;
		cycle(colHudX, y, () -> "HUD-Styles: " + onOff(cfg.hudEnabled), () -> cfg.hudEnabled = !cfg.hudEnabled);
		y += ROW;
		cycle(colHudX, y, () -> "FPS: " + onOff(cfg.hudFps), () -> cfg.hudFps = !cfg.hudFps);
		y += ROW;
		cycle(colHudX, y, () -> "Koordinaten: " + onOff(cfg.hudCoords), () -> cfg.hudCoords = !cfg.hudCoords);
		y += ROW;
		cycle(colHudX, y, () -> "Hintergrund: " + onOff(cfg.hudBackground), () -> cfg.hudBackground = !cfg.hudBackground);
		y += ROW;
		cycle(colHudX, y, () -> "Akzent: " + StylesConfig.colorName(cfg.hudAccent),
			() -> cfg.hudAccent = StylesConfig.nextColor(cfg.hudAccent, false));
		y += ROW;
		cycle(colHudX, y, () -> "Fadenkreuz: " + crosshairName(cfg.crosshairStyle),
			() -> cfg.crosshairStyle = (cfg.crosshairStyle + 1) % 3);
		y += ROW;
		cycle(colHudX, y, () -> "Fadenkreuz-Farbe: " + StylesConfig.colorName(cfg.crosshairColor),
			() -> cfg.crosshairColor = StylesConfig.nextColor(cfg.crosshairColor, false));

		// ---- Menü ----
		y = TOP;
		cycle(colMenuX, y, () -> "Menü-Styles: " + onOff(cfg.menuEnabled), () -> cfg.menuEnabled = !cfg.menuEnabled);
		y += ROW;
		cycle(colMenuX, y, () -> "Pause-Menü: " + onOff(cfg.menuPauseScreen), () -> cfg.menuPauseScreen = !cfg.menuPauseScreen);
		y += ROW;
		cycle(colMenuX, y, () -> "Farbe: " + StylesConfig.colorName(cfg.menuTint),
			() -> cfg.menuTint = StylesConfig.nextColor(cfg.menuTint, false));
		y += ROW;
		cycle(colMenuX, y, () -> "Stärke: " + cfg.menuTintStrength + " %",
			() -> cfg.menuTintStrength = cfg.menuTintStrength >= 100 ? 0 : cfg.menuTintStrength + 10);

		// ---- Fertig ----
		addDrawableChild(ButtonWidget.builder(Text.literal("Style-Browser"), b -> client.setScreen(new StyleBrowserScreen(this)))
			.dimensions(width / 2 - 102, height - 28, 100, 20)
			.build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Fertig"), b -> close())
			.dimensions(width / 2 + 2, height - 28, 100, 20)
			.build());
	}

	private void cycle(int x, int y, Supplier<String> label, Runnable onClick) {
		addDrawableChild(ButtonWidget.builder(Text.literal(label.get()), b -> {
			onClick.run();
			b.setMessage(Text.literal(label.get()));
		}).dimensions(x, y, COL_W, 20).build());
	}

	private static String onOff(boolean value) {
		return value ? "An" : "Aus";
	}

	private static String crosshairName(int style) {
		return switch (style) {
			case 1 -> "Punkt";
			case 2 -> "Kreuz";
			default -> "Aus";
		};
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		context.drawTextWithShadow(textRenderer, title, width / 2 - textRenderer.getWidth(title) / 2, 12, 0xFFFFFFFF);

		context.drawTextWithShadow(textRenderer, Text.literal("Chat"), colChatX, TOP - 14, 0xFF55FFFF);
		context.drawTextWithShadow(textRenderer, Text.literal("HUD"), colHudX, TOP - 14, 0xFF55FFFF);
		context.drawTextWithShadow(textRenderer, Text.literal("Menü"), colMenuX, TOP - 14, 0xFF55FFFF);

		context.drawTextWithShadow(textRenderer, Text.literal("Präfix:"), colChatX, prefixLabelY, 0xFFAAAAAA);
	}

	@Override
	public void removed() {
		StylesConfig.save();
	}

	@Override
	public void close() {
		client.setScreen(parent);
	}
}
