package com.happyzleaf.pixelmonwikigui.helper;

import com.happyzleaf.pixelmonwikigui.PixelmonWikiGUI;
import com.pixelmonmod.pixelmon.blocks.ranch.BreedingConditions;
import com.pixelmonmod.pixelmon.entities.npcs.registry.DropItemRegistry;
import com.pixelmonmod.pixelmon.entities.npcs.registry.PokemonDropInformation;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.EnumType;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class ReflectionHelper {
	private static Method getMethod(Class<?> clazz, String methodName) {
		try {
			Method method = clazz.getDeclaredMethod(methodName);
			method.setAccessible(true);
			return method;
		} catch (Exception e) {
			PixelmonWikiGUI.LOGGER.error("Couldn't load the method '{}' from the class '{}'.", methodName, clazz.getSimpleName());
			return null;
		}
	}

	private static <T> T invokeMethod(Method method, Object clazzInstance, Object... args) {
		if (method == null) {
			return null;
		}

		try {
			//noinspection unchecked
			return (T) method.invoke(clazzInstance, args);
		} catch (Exception e) {
			PixelmonWikiGUI.LOGGER.error("Couldn't invoke the method '{}' from the instance '{}'", method.toString(), clazzInstance);
			return null;
		}
	}

	private static Field getField(Class<?> clazz, String... names) {
		for (String name : names) {
			try {
				Field field = clazz.getDeclaredField(name);
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException ignored) {
			}
		}

		PixelmonWikiGUI.LOGGER.error("No fields '{}' found for class '{}'.", Arrays.toString(names), clazz.getSimpleName());
		return null;
	}

	private static <T> T getFieldInstance(Field field, Object clazzInstance) {
		if (field == null) {
			return null;
		}

		try {
			//noinspection unchecked
			return (T) field.get(clazzInstance);
		} catch (Exception e) {
			PixelmonWikiGUI.LOGGER.error("Couldn't retrieve the value from the field '{}' in the instance '{}'", field.toString(), clazzInstance);
			return null;
		}
	}

	private static Field typeBlockList_f = null;

	public static Optional<EnumMap<EnumType, HashMap<Block, Integer>>> getBreedingTypeList() {
		if (typeBlockList_f == null) {
			typeBlockList_f = getField(BreedingConditions.class, "typeBlockList");
		}

		EnumMap<EnumType, HashMap<Block, Integer>> typeBlockList = getFieldInstance(typeBlockList_f, null);
		if (typeBlockList == null || !typeBlockList.isEmpty()) {
			return Optional.ofNullable(typeBlockList);
		}

		new BreedingConditions(new ArrayList<>());
		return getBreedingTypeList();
	}

	private static Field biomeName_f = null;

	public static Optional<String> getBiomeName(Biome biome) {
		if (biomeName_f == null) {
			biomeName_f = getField(Biome.class, "field_76791_y", "biomeName");
		}

		return Optional.ofNullable(getFieldInstance(biomeName_f, biome));
	}

	public enum DropType {
		MAIN("Main drops:", "mainDrop", "mainDropMin", "mainDropMax"),
		RARE("Rare drops:", "rareDrop", "rareDropMin", "rareDropMax"),
		OPTIONAL_1("Optional drops:", "optDrop1", "optDrop1Min", "optDrop1Max"),
		OPTIONAL_2("Optional drops:", "optDrop2", "optDrop2Min", "optDrop2Max");

		public static final DropType[] ORDERED_VALUES = values();

		private final Map<EnumSpecies, Set<String>> cache = new HashMap<>();

		public final String displayName;

		private final String itemStack_name, min_name, max_name;
		private Field itemStack_f, min_f, max_f;

		DropType(String displayName, String itemStack_name, String min_name, String max_name) {
			this.displayName = TextFormatting.RESET + TextFormatting.WHITE.toString() + displayName;
			this.itemStack_name = itemStack_name;
			this.min_name = min_name;
			this.max_name = max_name;
		}

		public Set<String> getDrops(EnumSpecies species) {
			if (cache.containsKey(species)) {
				return cache.get(species);
			}

			if (itemStack_f == null) {
				itemStack_f = getField(PokemonDropInformation.class, itemStack_name);
				min_f = getField(PokemonDropInformation.class, min_name);
				max_f = getField(PokemonDropInformation.class, max_name);
			}

			Set<PokemonDropInformation> drops = DropItemRegistry.pokemonDrops.get(species);
			if (drops == null || drops.isEmpty()) {
				cache.put(species, Collections.emptySet());
				return Collections.emptySet();
			}

			Set<String> results = new HashSet<>();
			for (PokemonDropInformation drop : drops) {
				ItemStack is = getFieldInstance(itemStack_f, drop);
				if (is != null) {
					Integer min = getFieldInstance(min_f, drop);
					if (min == null) {
						min = 0;
					}
					Integer max = getFieldInstance(max_f, drop);
					if (max == null) {
						max = 1;
					}

					results.add(TextFormatting.GRAY + is.getDisplayName() + " " + TextFormatting.WHITE + min + "-" + max);
				}
			}

			cache.put(species, results);
			return results;
		}
	}
}
