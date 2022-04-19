package com.pm.aiost.misc.command.commands;

import static com.pm.aiost.misc.command.CommandHelper.hasArgSize;
import static com.pm.aiost.misc.command.CommandHelper.isAdmin;
import static com.pm.aiost.misc.command.CommandHelper.isPlayerOrConsole;

import java.io.File;

import com.pm.aiost.misc.BungeeConfigManager;
import com.pm.aiost.misc.ConfigManager;
import com.pm.aiost.misc.resourcePack.ResourcePackBuilder;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ResourcePackCommands {

	public static class BuildResourcePackCommand extends Command {

		public BuildResourcePackCommand() {
			super("buildResourcePack", null, new String[0]);
		}

		@Override
		public void execute(CommandSender sender, String[] args) {
			if (hasArgSize(sender, args, 0)) {
				if (isPlayerOrConsole(sender)) {
					if (isAdmin(sender)) {
						String itemsFilePath = ConfigManager.getConfigFolderPath() + File.separator + "Items.yml";
						File itemsFile = new File(itemsFilePath);
						ResourcePackBuilder.checkResourcePack_(BungeeConfigManager.loadConfig(itemsFile));
					}
				}
			}
		}
	}
}
