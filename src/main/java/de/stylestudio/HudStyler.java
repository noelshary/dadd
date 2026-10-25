package de.stylestudio;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public final class HudStyler {
	private HudStyler() {
	}

	public static void render(DrawContext ctx) {
		StylesConfig cfg = StylesConfig.get();
		MinecraftClient mc = MinecraftClient.getInstance();
		if (!cfg.hudEnabled || mc.player == null || mc.options.hudHidden) {
			return;
		}

		renderInfoPanel(ctx, mc, cfg);
		renderCrosshair(ctx, mc, cfg);
	}

	private static void renderInfoPanel(DrawContext ctx, MinecraftClient mc, StylesConfig cfg) {
		List<String> lines = new ArrayList<>();
		if (cfg.hudFps) {
			lines.add(mc.getCurrentFps() + " FPS");
		}
		if (cfg.hudCoords) {
			lines.add("XYZ: " + mc.player.getBlockX() + " / " + mc.player.getBlockY() + " / " + mc.player.getBlockZ());
		}
		if (lines.isEmpty()) {
			return;
		}

		TextRenderer tr = mc.textRenderer;
		int lineHeight = 10;
		int width = 0;
		for (String line : lines) {
			width = Math.max(width, tr.getWidth(line));
		}
		int height = lines.size() * lineHeight - 1;
		int x = 8;
		int y = 8;

		if (cfg.hudBackground) {
			ctx.fill(x - 4, y - 3, x + width + 4, y + height + 3, 0x90000000);
		}
		ctx.fill(x - 4, y - 3, x - 2, y + height + 3, 0xFF000000 | cfg.hudAccent);

		int textY = y;
		for (String line : lines) {
			ctx.drawTextWithShadow(tr, Text.literal(line), x, textY, 0xFFFFFFFF);
			textY += lineHeight;
		}
	}

	private static void renderCrosshair(DrawContext ctx, MinecraftClient mc, StylesConfig cfg) {
		if (cfg.crosshairStyle == 0 || !mc.options.getPerspective().isFirstPerson() || mc.currentScreen != null) {
			return;
		}

		int cx = ctx.getScaledWindowWidth() / 2;
		int cy = ctx.getScaledWindowHeight() / 2;
		int color = 0xFF000000 | cfg.crosshairColor;

		if (cfg.crosshairStyle == 1) {
			ctx.fill(cx - 1, cy - 1, cx + 1, cy + 1, color);
		} else {
			ctx.fill(cx - 7, cy - 1, cx - 2, cy, color);
			ctx.fill(cx + 1, cy - 1, cx + 6, cy, color);
			ctx.fill(cx - 1, cy - 7, cx, cy - 2, color);
			ctx.fill(cx - 1, cy + 1, cx, cy + 6, color);
		}
	}
}
