package com.happyzleaf.pixelmonwikigui.helper;

//import com.happyzleaf.pixelmonwikigui.helper.ScalaPixelmonUtil;

import com.mishkapp.minecraft.pixelmonwikigui.ScalaPixelmonUtil;
import com.pixelmonmod.pixelmon.api.pokemon.PokemonSpec;
import com.pixelmonmod.pixelmon.api.world.WeatherType;
import com.pixelmonmod.pixelmon.api.world.WorldTime;
import com.pixelmonmod.pixelmon.config.PixelmonItems;
import com.pixelmonmod.pixelmon.entities.npcs.registry.DropItemRegistry;
import com.pixelmonmod.pixelmon.entities.npcs.registry.PokemonDropInformation;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.BaseStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.StatsType;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.EnumType;
import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import java.util.*;
import java.util.stream.Collectors;

/**
 * All the methods starting from {@link PixelmonHelper#getTypeColor(EnumType)}
 * have been taken from the original plugin and converted to java.
 *
 * They're not that good, but they work and I won't change them until they break.
 */
public class PixelmonHelper {
	public static final ScalaPixelmonUtil SCALA_UTILS = new ScalaPixelmonUtil();

	public static final Set<String> POKEMON_NAMES = Arrays.stream(EnumSpecies.values()).map(EnumSpecies::name).collect(Collectors.toSet());

	private static List<String> NONE = Collections.singletonList(TextFormatting.DARK_GRAY + "None");

	public static ItemStack getPhoto(EnumSpecies species) {
		BaseStats stats = species.getBaseStats();

		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setShort("ndex", (short) species.getNationalPokedexInteger());
		nbt.setByte("form", (byte) stats.form);
		nbt.setByte("gender", (stats.malePercent > 0 ? Gender.Male : Gender.Female).getForm());

		ItemStack is = new ItemStack(PixelmonItems.itemPixelmonSprite);
		is.setTagCompound(nbt);
		return is;
	}

	public static EnumSpecies getSpecies(PokemonSpec spec) {
		EnumSpecies species = null;
		if (spec.name != null) {
			species = EnumSpecies.getFromNameAnyCaseNoTranslate(spec.name);
		}

		if (species == null) {
			throw new RuntimeException(String.format("The spec '%s' does not contain a name.", spec));
		}

		return species;
	}

	public static EnumSpecies parsePokemon(String nameOrId) throws CommandException {
		try {
			int id = Integer.parseInt(nameOrId);

			EnumSpecies species = EnumSpecies.getFromDex(id);
			if (species == null) {
				throw new CommandException(String.format("The id '%d' does not correspond to a pokémon.", id));
			}

			return species;
		} catch (NumberFormatException ignored) {
		}

		EnumSpecies species = EnumSpecies.getFromNameAnyCaseNoTranslate(nameOrId);
		if (species == null) {
			throw new CommandException(String.format("The name '%s' does not correspond to a pokémon.", nameOrId));
		}

		return species;
	}

	public static List<String> getDrops(EnumSpecies species) {
		List<String> description = new ArrayList<>();
		for (ReflectionHelper.DropType type : ReflectionHelper.DropType.ORDERED_VALUES) {
			Set<String> drops = type.getDrops(species);
			if (!drops.isEmpty()) {
				description.add("");
				description.add(type.displayName);
				description.addAll(drops);
			}
		}

		return description.isEmpty() ? NONE : description;
	}

	public static TextFormatting getTypeColor(EnumType type) {
		switch (type) {
			case Fire:
				return TextFormatting.RED;
			case Water:
			case Flying:
				return TextFormatting.BLUE;
			case Electric:
				return TextFormatting.YELLOW;
			case Grass:
			case Bug:
				return TextFormatting.GREEN;
			case Ice:
			case Mystery:
				return TextFormatting.AQUA;
			case Fighting:
				return TextFormatting.DARK_RED;
			case Poison:
				return TextFormatting.DARK_PURPLE;
			case Ground:
				return TextFormatting.GOLD;
			case Psychic:
			case Fairy:
				return TextFormatting.LIGHT_PURPLE;
			case Rock:
				return TextFormatting.DARK_GRAY;
			case Ghost:
			case Steel:
				return TextFormatting.GRAY;
			case Dragon:
				return TextFormatting.DARK_BLUE;
			case Dark:
				return TextFormatting.BLACK;
			default:
				return TextFormatting.WHITE;
		}
	}

	public static TextFormatting getStatColor(StatsType stat) {
		switch (stat) {
			case HP:
				return TextFormatting.GREEN;
			case Attack:
				return TextFormatting.RED;
			case Defence:
				return TextFormatting.GOLD;
			case SpecialAttack:
				return TextFormatting.LIGHT_PURPLE;
			case SpecialDefence:
				return TextFormatting.YELLOW;
			case Speed:
				return TextFormatting.BLUE;
			default:
				return TextFormatting.WHITE;
		}
	}

	public static String formatTime(WorldTime time) {
		switch (time) {
			case AFTERNOON:
				return TextFormatting.GOLD + "Afternoon";
			case DAWN:
				return TextFormatting.DARK_RED + "Dawn";
			case DAY:
				return TextFormatting.YELLOW + "Day";
			case DUSK:
				return TextFormatting.DARK_PURPLE + "Dusk";
			case MIDDAY:
				return TextFormatting.WHITE + "Midday";
			case MIDNIGHT:
				return TextFormatting.BLACK + "Midnight";
			case MORNING:
				return TextFormatting.RED + "Morning";
			case NIGHT:
				return TextFormatting.DARK_BLUE + "Night";
			default:
				return "";
		}
	}

	public static String formatWeather(WeatherType weather) {
		switch (weather) {
			case CLEAR:
				return TextFormatting.YELLOW + "Clear";
			case RAIN:
				return TextFormatting.BLUE + "Rainy";
			case STORM:
				return TextFormatting.DARK_GRAY + "Storm";
			default:
				return "";
		}
	}

	private static String getTypesString(BaseStats stats) {
		String types = TextFormatting.GOLD + "Type: ";

		EnumType type = stats.getType1();
		types += getTypeColor(type) + type.getLocalizedName();

		type = stats.getType2();
		if (type != null) {
			types += TextFormatting.WHITE + " / " + getTypeColor(type) + type.getLocalizedName();
		}

		return types;
	}

	public static List<String> getTypes(BaseStats stats) {
		return Arrays.asList(
				getTypesString(stats),
				TextFormatting.AQUA + "Egg group" + (stats.eggGroups.length != 1 ? "s" : "") + ": " + TextFormatting.DARK_AQUA + Arrays.toString(stats.eggGroups),
				TextFormatting.DARK_BLUE + "Level range: " + TextFormatting.GOLD + stats.spawnLevel.toString() + "-" + (stats.spawnLevel + stats.spawnLevelRange)
		);
	}

	public static List<String> getBaseStats(BaseStats stats) {
		return stats.stats.entrySet().stream().map(e -> getStatColor(e.getKey()) + e.getKey().getLocalizedName() + ": " + e.getValue()).collect(Collectors.toList());
	}

	public static List<String> getEvolutions(BaseStats stats) {
		if (stats.evolutions.isEmpty()) {
			return NONE;
		}

		List<String> evolutions = new ArrayList<>(SCALA_UTILS.getEvolutions(stats));
		evolutions.add("");
		evolutions.add(TextFormatting.GREEN + "Click to get more info");
		return evolutions;
	}

	public static List<String> getBreeding(BaseStats stats) {
		EnumMap<EnumType, HashMap<Block, Integer>> typeBlockList = ReflectionHelper.getBreedingTypeList().orElse(null);
		if (typeBlockList == null) {
			return NONE;
		}

		List<String> description = new ArrayList<>();
		for (EnumType type : stats.getTypeList()) {
			HashMap<Block, Integer> blocks = typeBlockList.get(type);
			if (blocks == null || blocks.isEmpty()) {
				continue;
			}

			description.add("");

			String color = getTypeColor(type).toString();
			description.add(TextFormatting.BOLD.toString() + TextFormatting.UNDERLINE + color + type.getLocalizedName());

			for (Map.Entry<Block, Integer> entry : blocks.entrySet()) {
				description.add(color + entry.getKey().getLocalizedName() + " " + entry.getValue());
			}
		}
		return description;
	}
}
