package com.happyzleaf.pixelmonwikigui;

import com.happyzleaf.pixelmonwikigui.command.CommandWiki;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
		modid = PixelmonWikiGUI.MOD_ID, name = PixelmonWikiGUI.MOD_NAME, version = PixelmonWikiGUI.VERSION,
		dependencies = "required-after:pixelmon;required-after:gooeylibs;",
		acceptableRemoteVersions = "*", acceptedMinecraftVersions = "[1.12.2]", serverSideOnly = true
)
public class PixelmonWikiGUI {
	public static final String MOD_ID = "pixelmonwikigui";
	public static final String MOD_NAME = "PixelmonWikiGUI";
	public static final String VERSION = "1.1.6";

	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	@Mod.EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandWiki());
//		event.registerServerCommand(new CommandDropInfo());

		LOGGER.info(String.format("%s v%s loaded! This plugin was made by mishkapp and rewritten by happyzleaf. (https://happyzleaf.com/)", MOD_NAME, VERSION));
	}
}
