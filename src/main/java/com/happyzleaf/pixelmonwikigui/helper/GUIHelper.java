package com.happyzleaf.pixelmonwikigui.helper;

import ca.landonjw.gooeylibs.inventory.api.Button;
import com.pixelmonmod.pixelmon.enums.EnumSpecies;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import static com.happyzleaf.pixelmonwikigui.helper.PixelmonHelper.getPhoto;

public class GUIHelper {
	public static boolean isClick(ClickType type) {
		return type == ClickType.PICKUP || type == ClickType.PICKUP_ALL;
	}

	public static Button.Builder createEmptyButton(ItemStack is) {
		is.getOrCreateSubCompound("display").setTag("Lore", new NBTTagList());

		return Button.builder()
				.item(is)
				.displayName("");
	}

	public static Button.Builder createPokemonButton(EnumSpecies species) {
		return createEmptyButton(getPhoto(species))
				.displayName(String.format("\u00A7r\u00A7l[%s] %s", species.getNationalPokedexNumber(), species.getPokemonName()));
	}
}
