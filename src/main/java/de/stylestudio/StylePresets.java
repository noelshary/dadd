package de.stylestudio;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.List;

public final class StylePresets {
	/** hue: 0-360 = Zielfarbton, -1 = Farbton behalten. */
	public record Preset(String name, int hue, float minSat, float satMul, float valMul, int swatch) {
	}

	public static final List<Preset> ALL = List.of(
		new Preset("Original", -1, 0f, 1f, 1f, 0x888888),
		new Preset("Amethyst", 280, 0.55f, 1f, 1.0f, 0xAA55FF),
		new Preset("Blutrot", 0, 0.70f, 1f, 0.95f, 0xCC2222),
		new Preset("Feuer", 22, 0.80f, 1f, 1.10f, 0xFF7722),
		new Preset("Gold", 45, 0.75f, 1f, 1.10f, 0xFFCC22),
		new Preset("Smaragd", 140, 0.60f, 1f, 1.0f, 0x22CC55),
		new Preset("Eis", 190, 0.45f, 1f, 1.15f, 0x77DDFF),
		new Preset("Ozean", 215, 0.60f, 1f, 1.0f, 0x2266DD),
		new Preset("Neon-Pink", 320, 0.70f, 1.1f, 1.15f, 0xFF44CC),
		new Preset("Schwarz-Weiß", -1, 0f, 0f, 1f, 0xDDDDDD),
		new Preset("Schatten", -1, 0f, 0.4f, 0.55f, 0x333344)
	);

	private StylePresets() {
	}

	public static int recolor(int argb, Preset p) {
		int a = argb >>> 24;
		if (a == 0) {
			return argb;
		}
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = argb & 0xFF;
		float[] hsb = Color.RGBtoHSB(r, g, b, null);
		float h = hsb[0];
		float s = hsb[1];
		float v = hsb[2];

		if (p.hue() >= 0) {
			h = p.hue() / 360f;
			s = Math.min(1f, Math.max(s, p.minSat()) * p.satMul());
		} else {
			s = Math.min(1f, s * p.satMul());
		}
		v = Math.min(1f, v * p.valMul());

		int rgb = Color.HSBtoRGB(h, s, v) & 0xFFFFFF;
		return (a << 24) | rgb;
	}

	public static BufferedImage recolor(BufferedImage in, Preset p) {
		BufferedImage out = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < in.getHeight(); y++) {
			for (int x = 0; x < in.getWidth(); x++) {
				out.setRGB(x, y, recolor(in.getRGB(x, y), p));
			}
		}
		return out;
	}
}
