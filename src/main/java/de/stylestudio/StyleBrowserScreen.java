package de.stylestudio;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class StyleBrowserScreen extends Screen {
	private static final int CAT_W = 90;
	private static final int LIST_W = 150;
	private static final int PRE_W = 84;
	private static final int GAP = 8;
	private static final int TOP = 40;
	private static final String[] SCOPES = {"Auswahl", "Kategorie", "Alles"};

	private final Screen parent;
	private final StylesConfig cfg = StylesConfig.get();
	private final List<int[]> rows = new ArrayList<>();
	private final List<int[]> presetRects = new ArrayList<>();

	private int category = 0;
	private int selected = 0;
	private int page = 0;
	private int scope = 0;
	private int x0;
	private int pageLabelX;
	private int pageLabelY;
	private String pageLabel = "";
	private String status = "Wähle links eine Kategorie, dann Ziel und Stil.";

	public StyleBrowserScreen(Screen parent) {
		super(Text.literal("Style Studio – Style-Browser"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		rows.clear();
		presetRects.clear();

		int totalW = CAT_W + GAP + LIST_W + GAP + 2 * PRE_W + 4;
		x0 = width / 2 - totalW / 2;

		// ---- Kategorien ----
		int y = TOP;
		for (int i = 0; i < StyleTargets.CATEGORIES.size(); i++) {
			final int ci = i;
			String name = StyleTargets.CATEGORIES.get(i);
			Text label = i == category ? Text.literal(name).formatted(Formatting.YELLOW) : Text.literal(name);
			addDrawableChild(ButtonWidget.builder(label, b -> {
				category = ci;
				selected = 0;
				page = 0;
				clearAndInit();
			}).dimensions(x0, y, CAT_W, 20).build());
			y += 24;
		}

		// ---- Ziele der Kategorie ----
		List<Integer> idxs = StyleTargets.indicesIn(category);
		int perPage = Math.max(3, (height - TOP - 70) / 22);
		int pages = Math.max(1, (idxs.size() + perPage - 1) / perPage);
		page = Math.max(0, Math.min(page, pages - 1));
		int lx = x0 + CAT_W + GAP;
		int ly = TOP;
		for (int i = page * perPage; i < Math.min(idxs.size(), (page + 1) * perPage); i++) {
			final int local = i;
			StyleTargets.Target t = StyleTargets.ALL.get(idxs.get(i));
			Text label = i == selected ? Text.literal(t.name()).formatted(Formatting.YELLOW) : Text.literal(t.name());
			addDrawableChild(ButtonWidget.builder(label, b -> {
				selected = local;
				clearAndInit();
			}).dimensions(lx, ly, LIST_W, 20).build());
			rows.add(new int[] {lx, ly, idxs.get(i)});
			ly += 22;
		}
		if (pages > 1) {
			ButtonWidget prev = ButtonWidget.builder(Text.literal("<"), b -> {
				page--;
				clearAndInit();
			}).dimensions(lx, ly + 2, 24, 20).build();
			prev.active = page > 0;
			addDrawableChild(prev);

			ButtonWidget next = ButtonWidget.builder(Text.literal(">"), b -> {
				page++;
				clearAndInit();
			}).dimensions(lx + LIST_W - 24, ly + 2, 24, 20).build();
			next.active = page < pages - 1;
			addDrawableChild(next);

			pageLabel = (page + 1) + " / " + pages;
			pageLabelX = lx + LIST_W / 2 - textRenderer.getWidth(pageLabel) / 2;
			pageLabelY = ly + 8;
		} else {
			pageLabel = "";
		}

		// ---- Stile ----
		int px = lx + LIST_W + GAP;
		int count = StylePresets.ALL.size();
		int current = currentPreset(idxs);
		for (int k = 0; k < count; k++) {
			final int pk = k;
			int bx = px + (k % 2) * (PRE_W + 4);
			int by = TOP + (k / 2) * 22;
			String name = StylePresets.ALL.get(k).name();
			Text label = k == current ? Text.literal(name).formatted(Formatting.YELLOW) : Text.literal(name);
			addDrawableChild(ButtonWidget.builder(label, b -> applyPreset(pk)).dimensions(bx, by, PRE_W, 20).build());
			presetRects.add(new int[] {bx, by, k});
		}

		int scopeY = TOP + ((count + 1) / 2) * 22 + 6;
		addDrawableChild(ButtonWidget.builder(Text.literal("Gilt für: " + SCOPES[scope]), b -> {
			scope = (scope + 1) % SCOPES.length;
			b.setMessage(Text.literal("Gilt für: " + SCOPES[scope]));
		}).dimensions(px, scopeY, 2 * PRE_W + 4, 20).build());

		// ---- Unten ----
		int by = height - 28;
		int bx = width / 2 - 169;
		addDrawableChild(ButtonWidget.builder(Text.literal("Anwenden"), b -> apply())
			.dimensions(bx, by, 130, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Alles zurücksetzen"), b -> {
			cfg.styles.clear();
			apply();
			clearAndInit();
		}).dimensions(bx + 134, by, 100, 20).build());
		addDrawableChild(ButtonWidget.builder(Text.literal("Fertig"), b -> close())
			.dimensions(bx + 238, by, 100, 20).build());
	}

	private int currentPreset(List<Integer> idxs) {
		if (idxs.isEmpty()) {
			return -1;
		}
		int sel = Math.min(selected, idxs.size() - 1);
		String id = StyleTargets.ALL.get(idxs.get(sel)).id();
		return cfg.styles.getOrDefault(id, 0);
	}

	private void applyPreset(int presetIndex) {
		List<Integer> idxs = StyleTargets.indicesIn(category);
		List<Integer> targets = new ArrayList<>();
		if (scope == 0) {
			if (!idxs.isEmpty()) {
				targets.add(idxs.get(Math.min(selected, idxs.size() - 1)));
			}
		} else if (scope == 1) {
			targets.addAll(idxs);
		} else {
			for (int i = 0; i < StyleTargets.ALL.size(); i++) {
				targets.add(i);
			}
		}

		for (int gi : targets) {
			String id = StyleTargets.ALL.get(gi).id();
			if (presetIndex == 0) {
				cfg.styles.remove(id);
			} else {
				cfg.styles.put(id, presetIndex);
			}
		}
		status = StylePresets.ALL.get(presetIndex).name() + " für " + targets.size() + " Ziel(e) gewählt. Dann „Anwenden“.";
		clearAndInit();
	}

	private void apply() {
		StylesConfig.save();
		PackBuilder.Result result = PackBuilder.build(client);
		if (result.error() != null) {
			String err = result.error();
			status = "Fehler: " + (err.length() > 60 ? err.substring(0, 60) : err);
		} else if (!result.enabled()) {
			status = result.written() + " Texturen erstellt. Aktiviere „style-studio“ unter Optionen > Ressourcenpakete.";
		} else {
			status = result.written() + " Texturen erstellt, lade neu …";
			client.reloadResources();
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		context.drawTextWithShadow(textRenderer, title, width / 2 - textRenderer.getWidth(title) / 2, 12, 0xFFFFFFFF);
		context.drawTextWithShadow(textRenderer, Text.literal("Kategorie"), x0, TOP - 12, 0xFF55FFFF);
		context.drawTextWithShadow(textRenderer, Text.literal("Ziel"), x0 + CAT_W + GAP, TOP - 12, 0xFF55FFFF);
		context.drawTextWithShadow(textRenderer, Text.literal("Stil"), x0 + CAT_W + GAP + LIST_W + GAP, TOP - 12, 0xFF55FFFF);

		for (int[] r : rows) {
			StyleTargets.Target t = StyleTargets.ALL.get(r[2]);
			context.drawItem(new ItemStack(t.icon()), r[0] + 2, r[1] + 2);
			int idx = cfg.styles.getOrDefault(t.id(), 0);
			if (idx > 0 && idx < StylePresets.ALL.size()) {
				int swatch = 0xFF000000 | StylePresets.ALL.get(idx).swatch();
				context.fill(r[0] + LIST_W - 4, r[1] + 1, r[0] + LIST_W - 1, r[1] + 19, swatch);
			}
		}

		for (int[] r : presetRects) {
			int swatch = 0xFF000000 | StylePresets.ALL.get(r[2]).swatch();
			context.fill(r[0] + 1, r[1] + 1, r[0] + 4, r[1] + 19, swatch);
		}

		if (!pageLabel.isEmpty()) {
			context.drawTextWithShadow(textRenderer, Text.literal(pageLabel), pageLabelX, pageLabelY, 0xFFAAAAAA);
		}

		context.drawTextWithShadow(textRenderer, Text.literal(status),
			width / 2 - textRenderer.getWidth(status) / 2, height - 42, 0xFFFFFF55);
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
