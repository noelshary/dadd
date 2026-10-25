package de.stylestudio;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public final class StyleTargets {
	public record Target(String id, String name, int category, Item icon, List<String> textures) {
	}

	public static final List<String> CATEGORIES = List.of("Blöcke", "Waffen", "Rüstung", "Sonstiges");
	public static final List<Target> ALL = new ArrayList<>();

	private StyleTargets() {
	}

	private static void add(String id, String name, int cat, Item icon, String... textures) {
		ALL.add(new Target(id, name, cat, icon, List.of(textures)));
	}

	public static List<Integer> indicesIn(int category) {
		List<Integer> out = new ArrayList<>();
		for (int i = 0; i < ALL.size(); i++) {
			if (ALL.get(i).category() == category) {
				out.add(i);
			}
		}
		return out;
	}

	static {
		// ---- Blöcke ----
		add("obsidian", "Obsidian", 0, Items.OBSIDIAN, "block/obsidian");
		add("crying_obsidian", "Weinender Obsidian", 0, Items.CRYING_OBSIDIAN, "block/crying_obsidian");
		add("respawn_anchor", "Respawn-Anker", 0, Items.RESPAWN_ANCHOR,
			"block/respawn_anchor_top", "block/respawn_anchor_top_off", "block/respawn_anchor_bottom",
			"block/respawn_anchor_side0", "block/respawn_anchor_side1", "block/respawn_anchor_side2",
			"block/respawn_anchor_side3", "block/respawn_anchor_side4");
		add("netherite_block", "Netheritblock", 0, Items.NETHERITE_BLOCK, "block/netherite_block");
		add("ancient_debris", "Antiker Schrott", 0, Items.ANCIENT_DEBRIS,
			"block/ancient_debris_top", "block/ancient_debris_side");
		add("amethyst_block", "Amethystblock", 0, Items.AMETHYST_BLOCK, "block/amethyst_block");
		add("diamond_block", "Diamantblock", 0, Items.DIAMOND_BLOCK, "block/diamond_block");
		add("gold_block", "Goldblock", 0, Items.GOLD_BLOCK, "block/gold_block");
		add("emerald_block", "Smaragdblock", 0, Items.EMERALD_BLOCK, "block/emerald_block");
		add("iron_block", "Eisenblock", 0, Items.IRON_BLOCK, "block/iron_block");
		add("netherrack", "Netherrack", 0, Items.NETHERRACK, "block/netherrack");
		add("end_stone", "Endstein", 0, Items.END_STONE, "block/end_stone");
		add("glowstone", "Glowstone", 0, Items.GLOWSTONE, "block/glowstone");

		// ---- Waffen & Werkzeug ----
		add("iron_sword", "Eisenschwert", 1, Items.IRON_SWORD, "item/iron_sword");
		add("diamond_sword", "Diamantschwert", 1, Items.DIAMOND_SWORD, "item/diamond_sword");
		add("netherite_sword", "Netheritschwert", 1, Items.NETHERITE_SWORD, "item/netherite_sword");
		add("diamond_pickaxe", "Diamant-Spitzhacke", 1, Items.DIAMOND_PICKAXE, "item/diamond_pickaxe");
		add("netherite_pickaxe", "Netherit-Spitzhacke", 1, Items.NETHERITE_PICKAXE, "item/netherite_pickaxe");
		add("diamond_axe", "Diamantaxt", 1, Items.DIAMOND_AXE, "item/diamond_axe");
		add("netherite_axe", "Netheritaxt", 1, Items.NETHERITE_AXE, "item/netherite_axe");
		add("bow", "Bogen", 1, Items.BOW,
			"item/bow", "item/bow_pulling_0", "item/bow_pulling_1", "item/bow_pulling_2");
		add("mace", "Streitkolben", 1, Items.MACE, "item/mace");

		// ---- Rüstung & Schild ----
		add("shield", "Schild", 2, Items.SHIELD,
			"entity/shield_base", "entity/shield_base_nopattern",
			"entity/shield/shield_base", "entity/shield/shield_base_nopattern");
		add("iron_armor", "Eisenrüstung", 2, Items.IRON_CHESTPLATE,
			"item/iron_helmet", "item/iron_chestplate", "item/iron_leggings", "item/iron_boots",
			"entity/equipment/humanoid/iron", "entity/equipment/humanoid_leggings/iron");
		add("diamond_armor", "Diamantrüstung", 2, Items.DIAMOND_CHESTPLATE,
			"item/diamond_helmet", "item/diamond_chestplate", "item/diamond_leggings", "item/diamond_boots",
			"entity/equipment/humanoid/diamond", "entity/equipment/humanoid_leggings/diamond");
		add("netherite_armor", "Netheritrüstung", 2, Items.NETHERITE_CHESTPLATE,
			"item/netherite_helmet", "item/netherite_chestplate", "item/netherite_leggings", "item/netherite_boots",
			"entity/equipment/humanoid/netherite", "entity/equipment/humanoid_leggings/netherite");
		add("elytra", "Elytren", 2, Items.ELYTRA,
			"entity/equipment/wings/elytra", "entity/elytra", "item/elytra");

		// ---- Sonstiges ----
		add("totem", "Totem der Unsterblichkeit", 3, Items.TOTEM_OF_UNDYING, "item/totem_of_undying");
		add("ender_pearl", "Enderperle", 3, Items.ENDER_PEARL, "item/ender_pearl");
		add("golden_apple", "Goldener Apfel", 3, Items.GOLDEN_APPLE, "item/golden_apple");
		add("diamond", "Diamant", 3, Items.DIAMOND, "item/diamond");
		add("netherite_ingot", "Netheritbarren", 3, Items.NETHERITE_INGOT, "item/netherite_ingot");
		add("emerald", "Smaragd", 3, Items.EMERALD, "item/emerald");
		add("xp_bottle", "Erfahrungsfläschchen", 3, Items.EXPERIENCE_BOTTLE, "item/experience_bottle");
	}
}
