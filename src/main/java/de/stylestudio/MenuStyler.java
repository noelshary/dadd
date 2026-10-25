package de.stylestudio;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;

public final class MenuStyler {
	private MenuStyler() {
	}

	public static void onScreenInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
		boolean title = screen instanceof TitleScreen;
		boolean pause = screen instanceof GameMenuScreen;
		if (!title && !pause) {
			return;
		}
		ScreenEvents.afterRender(screen).register((s, ctx, mouseX, mouseY, delta) -> render(ctx, s, title));
	}

	private static void render(DrawContext ctx, Screen screen, boolean title) {
		StylesConfig cfg = StylesConfig.get();
		if (!cfg.menuEnabled || (!title && !cfg.menuPauseScreen)) {
			return;
		}

		int alpha = Math.round(Math.max(0, Math.min(100, cfg.menuTintStrength)) / 100f * 160f);
		int rgb = cfg.menuTint & 0xFFFFFF;
		int top = ((alpha / 3) << 24) | rgb;
		int bottom = (alpha << 24) | rgb;
		ctx.fillGradient(0, 0, screen.width, screen.height, top, bottom);

		ctx.fill(0, 0, screen.width, 2, 0xFF000000 | rgb);

		if (title) {
			String version = FabricLoader.getInstance()
				.getModContainer(StyleStudioClient.MOD_ID)
				.map(c -> c.getMetadata().getVersion().getFriendlyString())
				.orElse("");
			ctx.drawTextWithShadow(
				MinecraftClient.getInstance().textRenderer,
				Text.literal("Style Studio " + version),
				4, screen.height - 12, 0xFF000000 | rgb
			);
		}
	}
}
