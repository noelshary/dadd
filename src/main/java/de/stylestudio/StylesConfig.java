package de.stylestudio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class StylesConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("style-studio.json");
	private static StylesConfig instance = new StylesConfig();

	public static final int[] PALETTE = {
		0x55FFFF, 0xFFAA00, 0x55FF55, 0xFF5555, 0xFF55FF, 0xFFFF55, 0x5555FF, 0xFFFFFF, 0xAAAAAA
	};
	public static final String[] NAMES = {
		"Aqua", "Gold", "Grün", "Rot", "Pink", "Gelb", "Blau", "Weiß", "Grau"
	};

	// Chat
	public boolean chatEnabled = true;
	public boolean chatTimestamp = true;
	public int chatTimestampColor = 0xAAAAAA;
	public String chatPrefix = "";
	public int chatPrefixColor = 0x55FFFF;
	public int chatTextColor = -1; // -1 = Standard

	// HUD
	public boolean hudEnabled = true;
	public boolean hudFps = true;
	public boolean hudCoords = true;
	public boolean hudBackground = true;
	public int hudAccent = 0x55FFFF;
	public int crosshairStyle = 0; // 0 = aus, 1 = Punkt, 2 = Kreuz
	public int crosshairColor = 0xFFFFFF;

	// Resource-Pack-Styles: Ziel-ID -> Preset-Index (0 = Original)
	public java.util.Map<String, Integer> styles = new java.util.HashMap<>();

	// Menü
	public boolean menuEnabled = true;
	public boolean menuPauseScreen = false;
	public int menuTint = 0x55FFFF;
	public int menuTintStrength = 40; // 0-100

	public static StylesConfig get() {
		return instance;
	}

	public static void load() {
		if (Files.exists(FILE)) {
			try (Reader reader = Files.newBufferedReader(FILE)) {
				StylesConfig loaded = GSON.fromJson(reader, StylesConfig.class);
				if (loaded != null) {
					instance = loaded;
				}
			} catch (Exception e) {
				System.err.println("[Style Studio] Konfiguration konnte nicht gelesen werden, nutze Standardwerte: " + e);
			}
		}
		save();
	}

	public static void save() {
		try (Writer writer = Files.newBufferedWriter(FILE)) {
			GSON.toJson(instance, writer);
		} catch (Exception e) {
			System.err.println("[Style Studio] Konfiguration konnte nicht gespeichert werden: " + e);
		}
	}

	public static int nextColor(int current, boolean allowNone) {
		if (allowNone && current == -1) {
			return PALETTE[0];
		}
		for (int i = 0; i < PALETTE.length; i++) {
			if (PALETTE[i] == current) {
				if (i + 1 < PALETTE.length) {
					return PALETTE[i + 1];
				}
				return allowNone ? -1 : PALETTE[0];
			}
		}
		return allowNone ? -1 : PALETTE[0];
	}

	public static String colorName(int rgb) {
		if (rgb == -1) {
			return "Standard";
		}
		for (int i = 0; i < PALETTE.length; i++) {
			if (PALETTE[i] == rgb) {
				return NAMES[i];
			}
		}
		return String.format("#%06X", rgb & 0xFFFFFF);
	}
}
