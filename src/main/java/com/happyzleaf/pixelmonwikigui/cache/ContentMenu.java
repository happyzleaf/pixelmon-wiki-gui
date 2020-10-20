package com.happyzleaf.pixelmonwikigui.cache;

import ca.landonjw.gooeylibs.inventory.api.Button;
import ca.landonjw.gooeylibs.inventory.api.LineType;
import ca.landonjw.gooeylibs.inventory.api.Page;
import ca.landonjw.gooeylibs.inventory.api.Template;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;

import static com.happyzleaf.pixelmonwikigui.helper.GUIHelper.*;

public class ContentMenu {
	private static final Button RED_PANE = createEmptyButton(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.RED.getMetadata())).build();
	private static final Button WHITE_PANE = createEmptyButton(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.WHITE.getMetadata())).build();
	private static final Button BLACK_PANE = createEmptyButton(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.BLACK.getMetadata())).build();

	private static final Button GRAY_PANE = createEmptyButton(new ItemStack(Blocks.STAINED_GLASS_PANE, 1, EnumDyeColor.GRAY.getMetadata())).build();

	@SuppressWarnings("unchecked")
	private static final Pair<Integer, Integer>[] CONTENT_SPACE = (Pair<Integer, Integer>[]) new Pair[]{
			Pair.of(1, 3),
			Pair.of(1, 4),
			Pair.of(1, 5),
			Pair.of(1, 6),
			Pair.of(1, 7),
			Pair.of(2, 3),
			Pair.of(2, 4),
			Pair.of(2, 5),
			Pair.of(2, 6),
			Pair.of(2, 7),
			Pair.of(3, 3),
			Pair.of(3, 4),
			Pair.of(3, 5),
			Pair.of(3, 6),
			Pair.of(3, 7)
	};

	public static final int MAX_CONTENT = CONTENT_SPACE.length;

	private static Button createRelativeButton(EnumSpecies species, int step) {
		EnumSpecies relative = EnumSpecies.getFromDex(species.getNationalPokedexInteger() + step);
		if (relative == null) {
			return GRAY_PANE;
		}

		return createPokemonButton(relative)
				.lore(Collections.singletonList(TextFormatting.GREEN + "Click to get more info"))
				.onClick(action -> {
					if (isClick(action.getClickType())) {
						ContentProvider.GENERAL.open(relative, action.getPlayer());
					}
				})
				.build();
	}

	public static Page getPage(EnumSpecies species, ContentProvider provider) {
		Button[] content = provider.getContent(species);
		if (content == null || content.length > MAX_CONTENT) {
			throw new RuntimeException("The content provided exceeds 15 elements or is invalid.");
		}

		Template.Builder page = Template.builder(5)
				// Red Frame
				.line(LineType.Horizontal, 0, 0, 9, RED_PANE)
				.set(1, 0, RED_PANE)
				.set(1, 2, RED_PANE)
				.set(1, 8, RED_PANE)
				// Black Frame
				.set(2, 0, BLACK_PANE)
				.set(2, 2, BLACK_PANE)
				.set(2, 8, BLACK_PANE)
				// White Frame
				.set(3, 0, WHITE_PANE)
				.set(3, 2, WHITE_PANE)
				.set(3, 8, WHITE_PANE)
				.line(LineType.Horizontal, 4, 0, 9, WHITE_PANE)

				// Controller
				.set(1, 1, createRelativeButton(species, -1))
				.set(2, 1, createPokemonButton(species).build())
				.set(3, 1, createRelativeButton(species, +1));

		for (int i = 0; i < content.length; i++) {
			page.set(CONTENT_SPACE[i].getLeft(), CONTENT_SPACE[i].getRight(), content[i]);
		}

		return Page.builder()
				.title("\u00A7l         Pixelmon \u00A7l\u00A7cWiki")
				.template(page.build())
				.build();
	}
}
