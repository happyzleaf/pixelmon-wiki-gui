package com.happyzleaf.pixelmonwikigui.cache;

import ca.landonjw.gooeylibs.inventory.api.Button;
import ca.landonjw.gooeylibs.inventory.api.Page;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.happyzleaf.pixelmonwikigui.PixelmonWikiGUI;
import com.pixelmonmod.pixelmon.api.world.WorldTime;
import com.pixelmonmod.pixelmon.battles.attacks.AttackBase;
import com.pixelmonmod.pixelmon.config.*;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.BaseStats;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.Gender;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.Evolution;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.conditions.*;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.types.InteractEvolution;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.types.LevelingEvolution;
import com.pixelmonmod.pixelmon.entities.pixelmon.stats.evolution.types.TradeEvolution;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import com.pixelmonmod.pixelmon.enums.EnumType;
import com.pixelmonmod.pixelmon.enums.technicalmoves.Gen4TechnicalMachines;
import com.pixelmonmod.pixelmon.enums.technicalmoves.Gen5TechnicalMachines;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.happyzleaf.pixelmonwikigui.helper.GUIHelper.createPokemonButton;
import static com.happyzleaf.pixelmonwikigui.helper.GUIHelper.isClick;
import static com.happyzleaf.pixelmonwikigui.helper.PixelmonHelper.*;

public enum ContentProvider {
	GENERAL {
		@Override
		public Button[] getContent(EnumSpecies species) {
			BaseStats stats = species.getBaseStats();

			Button[] content = new Button[15];
			int i = 0;

			content[i++] = Button.builder()
					.item(removeLore(new ItemStack(PixelmonItemsBadges.marshBadge)))
					.displayName("\u00A7r\u00A7lType")
					.lore(getTypes(stats))
					.build();

			content[i++] = Button.builder()
					.item(removeLore(new ItemStack(PixelmonItemsHeld.weaknessPolicy)))
					.displayName("\u00A7r\u00A7lBase Stats")
					.lore(getBaseStats(stats))
					.build();

			content[i++] = Button.builder()
					.item(new ItemStack(Blocks.SAPLING))
					.displayName("\u00A7r\u00A7lSpawn Biomes")
					.lore(SCALA_UTILS.getSpawnBiomes(stats))
					.build();

			content[i++] = Button.builder()
					.item(new ItemStack(Items.CLOCK))
					.displayName("\u00A7r\u00A7lSpawn Time")
					.lore(SCALA_UTILS.getSpawnTimes(stats))
					.build();

			content[i++] = Button.builder()
					.item(new ItemStack(Items.PAPER))
					.displayName("\u00A7r\u00A7lSpawn Location")
					.lore(SCALA_UTILS.getSpawnLocation(stats))
					.build();

			content[i++] = Button.builder()
					.item(removeLore(new ItemStack(PixelmonItemsPokeballs.pokeBall)))
					.displayName("\u00A7r\u00A7lCatch Rate")
					.lore(SCALA_UTILS.getCatchRate(stats))
					.build();

			content[i++] = Button.builder()
					.item(removeLore(new ItemStack(PixelmonItems.abilityCapsule)))
					.displayName("\u00A7r\u00A7lAbilities")
					.lore(SCALA_UTILS.getAbilities(stats))
					.build();

			content[i++] = Button.builder()
					.item(removeLore(new ItemStack(PixelmonItemsHeld.powerWeight)))
					.displayName("\u00A7r\u00A7lEV Yield")
					.lore(SCALA_UTILS.getEVYield(stats))
					.build();

			content[i++] = Button.builder()
					.item(new ItemStack(Items.DIAMOND))
					.displayName("\u00A7r\u00A7lDrops")
					.lore(getDrops(species))
					.build();

			content[i++] = Button.builder()
					.item(removeLore(new ItemStack(PixelmonItemsHeld.upGrade)))
					.displayName("\u00A7r\u00A7lEvolutions")
					.lore(getEvolutions(stats))
					.onClick(action -> {
						if (!stats.evolutions.isEmpty() && isClick(action.getClickType())) {
							EVOLUTIONS.open(species, action.getPlayer());
						}

					})
					.build();

			content[i++] = Button.builder()
					.item(removeLore(PixelmonItemsTMs.createStackFor(Gen4TechnicalMachines.Dragon_Claw)))
					.displayName("\u00A7r\u00A7lTutor Moves")
					.lore(SCALA_UTILS.getTutorMoves(stats))
					.build();

			content[i++] = Button.builder()
					.item(removeLore(new ItemStack(PixelmonItemsTMs.HMs.get(0))))
					.displayName("\u00A7r\u00A7lTM/HM Moves")
					.lore(SCALA_UTILS.getTMMoves(stats))
					.build();

			content[i++] = Button.builder()
					.item(removeLore(PixelmonItemsTMs.createStackFor(Gen5TechnicalMachines.Incinerate)))
					.displayName("\u00A7r\u00A7lMoves by Level")
					.lore(SCALA_UTILS.getMoves(stats))
					.build();

			content[i++] = Button.builder()
					.item(removeLore(new ItemStack(PixelmonItemsBadges.rumbleBadge)))
					.displayName("\u00A7r\u00A7lEffectiveness")
					.lore(SCALA_UTILS.getTypeEffectiveness(stats))
					.build();

			content[i++] = Button.builder()
					.item(removeLore(new ItemStack(PixelmonItems.ranchUpgrade)))
					.displayName("\u00A7r\u00A7lBreeding Environment")
					.lore(getBreeding(stats))
					.build();

			return content;
		}
	},
	EVOLUTIONS {
		@Override
		public Button[] getContent(EnumSpecies species) {
			BaseStats stats = species.getBaseStats();

			int length = Math.min(stats.evolutions.size(), ContentMenu.MAX_CONTENT);
			Button[] content = new Button[length];

			for (int i = 0; i < length; i++) {
				Evolution evolution = stats.evolutions.get(i);

				List<String> description = new ArrayList<>();
				description.add("");

				if (evolution instanceof LevelingEvolution) {
					description.add("\u00A7r\u00A77Evolves at level " + ((LevelingEvolution) evolution).getLevel());
				} else if (evolution instanceof InteractEvolution) {
					description.add("\u00A7r\u00A77Evolves when holding \u00A76" + ((InteractEvolution) evolution).item.getItemStack().getDisplayName());
				} else if (evolution instanceof TradeEvolution) {
					String onTrade = "\u00A7r\u00A77Evolves when traded";
					if (((TradeEvolution) evolution).with != null) {
						onTrade += " with \u00A79" + ((TradeEvolution) evolution).with.getPokemonName();
					}
					description.add(onTrade);
				}

				for (EvoCondition condition : evolution.conditions) {
					if (condition instanceof BiomeCondition) {
						description.add("\u00A7r\u00A77Biomes: \u00A7b" + ((BiomeCondition) condition).biomes.toString());
					} else if (condition instanceof ChanceCondition) {
						description.add("\u00A7r\u00A77Chance: \u00A76" + (((ChanceCondition) condition).chance * 100) + "%");
					} else if (condition instanceof EvoRockCondition) {
						description.add("\u00A7r\u00A77Near: \u00A7a" + ((EvoRockCondition) condition).evolutionRock);
					} else if (condition instanceof FriendshipCondition) {
						description.add("\u00A7r\u00A77Friendship: \u00A7d" + ((FriendshipCondition) condition).friendship);
					} else if (condition instanceof GenderCondition) {
						Gender gender = ((GenderCondition) condition).genders.get(0);
						description.add("\u00A7r\u00A77Gender: \u00A7" + (gender == Gender.Male ? "3" : "5") + gender.getLocalizedName());
					} else if (condition instanceof HeldItemCondition) {
						description.add("\u00A7r\u00A77Holding: \u00A76" + ((HeldItemCondition) condition).item.getItemStack().getDisplayName());
					} else if (condition instanceof HighAltitudeCondition) {
						description.add("\u00A7r\u00A77Minimum altitude: \u00A76" + (int) ((HighAltitudeCondition) condition).minAltitude);
					} else if (condition instanceof MoveCondition) {
						description.add("\u00A7r\u00A77Must know move: \u00A79" + AttackBase.getAttackBase(((MoveCondition) condition).attackIndex).get().getLocalizedName());
					} else if (condition instanceof MoveTypeCondition) {
						EnumType type = ((MoveTypeCondition) condition).type;
						description.add("\u00A7r\u00A77Must know move type: " + getTypeColor(type) + type.getLocalizedName());
					} else if (condition instanceof StatRatioCondition) {
						description.add("\u00A7r\u00A7e" + ((StatRatioCondition) condition).stat1.getLocalizedName() + " > " + ((StatRatioCondition) condition).stat2.getLocalizedName());
					} else if (condition instanceof PartyCondition) {
						if (!((PartyCondition) condition).withPokemon.isEmpty()) {
							description.add("\u00A7r\u00A77Must have these pok\u00E9mon with you: \u00E96" + ((PartyCondition) condition).withPokemon.toString());
						}
						if (!((PartyCondition) condition).withTypes.isEmpty()) {
							description.add("\u00A7r\u00A77Must have these pok\u00E9mon types with you: \u00E96" + ((PartyCondition) condition).withTypes.toString());
						}
					} else if (condition instanceof TimeCondition) {
						WorldTime time = ((TimeCondition) condition).time;
						if (time != null) {
							description.add("\u00A7r\u00A77During: " + formatTime(time));
						}
					} else if (condition instanceof WeatherCondition) {
						description.add("\u00A7r\u00A77Weather: " + formatWeather(((WeatherCondition) condition).weather));
					}
				}

				description.add("");
				description.add("\u00A7r\u00A7aClick to get more info");

				EnumSpecies evolutionSpecies = getSpecies(evolution.to);
				content[i] = createPokemonButton(evolutionSpecies)
						.lore(description)
						.onClick(action -> {
							if (isClick(action.getClickType())) {
								GENERAL.open(evolutionSpecies, action.getPlayer());
							}
						})
						.build();
			}

			return content;
		}
	};

	private final ContentProvider instance = this;

	private final LoadingCache<EnumSpecies, Page> cache = CacheBuilder.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build(new CacheLoader<EnumSpecies, Page>() {
				@Override
				public Page load(EnumSpecies species) {
					return ContentMenu.getPage(species, instance);
				}
			});

	public abstract Button[] getContent(EnumSpecies species);

	public void open(EnumSpecies species, EntityPlayerMP player) {
		try {
			cache.get(species).forceOpenPage(player);
		} catch (ExecutionException e) {
			PixelmonWikiGUI.LOGGER.error(String.format("Couldn't load the wiki wiki page for '%s'.", species.name), e);
		}
	}
}
