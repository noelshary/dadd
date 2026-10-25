package de.stylestudio;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/** Erzeugt aus der Style-Auswahl ein echtes Resource Pack in resourcepacks/stylestudio-styles. */
public final class PackBuilder {
	public static final String PACK_NAME = "style-studio";

	public record Result(int written, boolean enabled, String error) {
	}

	private PackBuilder() {
	}

	public static Result build(MinecraftClient mc) {
		StylesConfig cfg = StylesConfig.get();
		Path root = FabricLoader.getInstance().getGameDir().resolve("resourcepacks").resolve(PACK_NAME);
		try {
			deleteRecursively(root.resolve("assets"));
			Files.createDirectories(root);
			Files.writeString(root.resolve("pack.mcmeta"),
				"{\n  \"pack\": {\n    \"pack_format\": 75,\n    \"description\": \"Style Studio - automatisch erzeugt\"\n  }\n}\n");

			int written = 0;
			for (StyleTargets.Target target : StyleTargets.ALL) {
				int idx = cfg.styles.getOrDefault(target.id(), 0);
				if (idx <= 0 || idx >= StylePresets.ALL.size()) {
					continue;
				}
				StylePresets.Preset preset = StylePresets.ALL.get(idx);
				for (String texture : target.textures()) {
					written += convert(mc, texture, preset, root);
				}
			}

			mc.getResourcePackManager().scanPacks();
			boolean enabled = mc.getResourcePackManager().getEnabledIds().contains("file/" + PACK_NAME);
			return new Result(written, enabled, null);
		} catch (Exception e) {
			return new Result(0, false, e.toString());
		}
	}

	private static int convert(MinecraftClient mc, String texture, StylePresets.Preset preset, Path root) throws IOException {
		Identifier id = Identifier.of("minecraft", "textures/" + texture + ".png");
		Resource resource = findVanilla(mc, id);
		if (resource == null) {
			return 0;
		}

		BufferedImage image;
		try (InputStream in = resource.getInputStream()) {
			image = ImageIO.read(in);
		}
		if (image == null) {
			return 0;
		}

		Path target = root.resolve("assets").resolve("minecraft").resolve("textures").resolve(texture + ".png");
		Files.createDirectories(target.getParent());
		ImageIO.write(StylePresets.recolor(image, preset), "png", target.toFile());

		// Animationsdaten (.mcmeta) mitkopieren, falls vorhanden
		Resource meta = findVanilla(mc, Identifier.of("minecraft", "textures/" + texture + ".png.mcmeta"));
		if (meta != null) {
			try (InputStream in = meta.getInputStream()) {
				Files.copy(in, target.resolveSibling(target.getFileName() + ".mcmeta"), StandardCopyOption.REPLACE_EXISTING);
			}
		}
		return 1;
	}

	/** Liefert die Original-Textur, nie die bereits umgefärbte aus unserem eigenen Pack. */
	private static Resource findVanilla(MinecraftClient mc, Identifier id) {
		List<Resource> all = mc.getResourceManager().getAllResources(id);
		for (Resource r : all) {
			if ("vanilla".equals(r.getPackId())) {
				return r;
			}
		}
		for (Resource r : all) {
			if (!r.getPackId().contains(PACK_NAME)) {
				return r;
			}
		}
		return null;
	}

	private static void deleteRecursively(Path dir) throws IOException {
		if (!Files.exists(dir)) {
			return;
		}
		try (Stream<Path> walk = Files.walk(dir)) {
			for (Path p : walk.sorted(Comparator.reverseOrder()).toList()) {
				Files.delete(p);
			}
		}
	}
}
