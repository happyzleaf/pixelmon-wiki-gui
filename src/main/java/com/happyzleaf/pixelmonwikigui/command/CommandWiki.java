package com.happyzleaf.pixelmonwikigui.command;

import com.happyzleaf.pixelmonwikigui.cache.ContentProvider;
import com.happyzleaf.pixelmonwikigui.helper.PixelmonHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandWiki extends CommandBase {
	@Override
	public String getName() {
		return "pwiki";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/pwiki <pokemon>";
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, PixelmonHelper.POKEMON_NAMES) : Collections.emptyList();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (!(sender instanceof EntityPlayerMP)) {
			throw new CommandException("You must be in-game to use this command.");
		}

		if (args.length != 1) {
			throw new CommandException(getUsage(sender));
		}

		ContentProvider.GENERAL.open(PixelmonHelper.parsePokemon(args[0]), (EntityPlayerMP) sender);
	}
}
