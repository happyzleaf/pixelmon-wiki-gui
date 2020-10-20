package com.happyzleaf.pixelmonwikigui.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class CommandDropInfo extends CommandBase {
	@Override
	public String getName() {
		return "dropinfo";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/dropinfo <item>";
	}

	private static Set<ResourceLocation> ITEM_NAMES = Item.REGISTRY.getKeys();

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, ITEM_NAMES) : Collections.emptyList();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length != 1) {
			throw new CommandException(getUsage(sender));
		}

		Item item = Item.REGISTRY.getObject(new ResourceLocation(args[0]));
		if (item == null) {
			throw new CommandException(String.format("No item found for id '%s'.", args[0]));
		}
	}
}

//	def getDropList(item:ItemType):Text={
//		val lists=EnumSpecies.values()
//		.map(p=>(p,Util.getDropInfo(p)))
//		.filter(t=>t._2!=null)
//		.filter(t=>t._2.dropList.exists(is=>is.getType.equals(item)))
//		.toList
//
//		if(lists.isEmpty){
//		return Text.of(TextColors.AQUA,item.getName,TextColors.RED," does not drops from any pokemon")
//		}
//
//		val builder=Text.builder()
//		builder.append(Text.of(TextColors.GOLD,"Drop list for ",TextColors.AQUA,item.getName,TextColors.GOLD,":"))
//		builder.append(Text.NEW_LINE)
//		lists
//		.map(t=>{
//		val pokemon=Text.of(TextColors.GOLD,Text.builder(t._1.getLocalizedName)
//		.onHover(TextActions.showText(Text.of("Open in wiki")))
//		.onClick(TextActions.runCommand(String.format("/pwiki %s",t._1.name().toLowerCase))).build())
//		val color=t._2.dropType(item)match{
//		case DropType.Main=>TextColors.GRAY
//		case DropType.Opt=>TextColors.AQUA
//		case DropType.Rare=>TextColors.GOLD
//		}
//		val quantity=t._2.quantity(item)
//		val b=Text.builder()
//		b.append(Text.of("  "))
//		b.append(pokemon)
//		val s:String=" ["+quantity._1+"-"+quantity._2+"]"
//		b.append(Text.of(s))
//		b.build()
//		})
//		.foreach(t=>{
//		builder.append(t)
//		builder.append(Text.NEW_LINE)
//		})
//		builder.build()
//		}
//		}
