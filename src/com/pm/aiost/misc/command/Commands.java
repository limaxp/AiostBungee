package com.pm.aiost.misc.command;

import com.pm.aiost.misc.command.commands.DatabaseCommands.BatchQueryCommand;
import com.pm.aiost.misc.command.commands.DatabaseCommands.BuildDatabaseCommand;
import com.pm.aiost.misc.command.commands.DatabaseCommands.CallQueryCommand;
import com.pm.aiost.misc.command.commands.DatabaseCommands.ExecuteQueryCommand;
import com.pm.aiost.misc.command.commands.DatabaseCommands.QueryCommand;
import com.pm.aiost.misc.command.commands.DatabaseCommands.UpdateQueryCommand;
import com.pm.aiost.misc.command.commands.ResourcePackCommands.BuildResourcePackCommand;
import com.pm.aiost.misc.command.commands.ServerCommands.CreateServerCommand;
import com.pm.aiost.misc.command.commands.ServerCommands.DeleteServerCommand;
import com.pm.aiost.misc.command.commands.ServerCommands.RegisterServerCommand;
import com.pm.aiost.misc.command.commands.ServerCommands.ReloadCommand;
import com.pm.aiost.misc.command.commands.ServerCommands.StartServerCommand;
import com.pm.aiost.misc.command.commands.ServerCommands.StopCommand;
import com.pm.aiost.misc.command.commands.ServerCommands.StopServerCommand;
import com.pm.aiost.misc.command.commands.ServerCommands.UnregisterServerCommand;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class Commands {

	private Commands() {
	}

	public static void linkCommands(Plugin plugin) {
		PluginManager pluginManager = plugin.getProxy().getPluginManager();

		pluginManager.registerCommand(plugin, new ReloadCommand());
		pluginManager.registerCommand(plugin, new StopCommand());
		pluginManager.registerCommand(plugin, new RegisterServerCommand());
		pluginManager.registerCommand(plugin, new UnregisterServerCommand());
		pluginManager.registerCommand(plugin, new CreateServerCommand());
		pluginManager.registerCommand(plugin, new DeleteServerCommand());
		pluginManager.registerCommand(plugin, new StartServerCommand());
		pluginManager.registerCommand(plugin, new StopServerCommand());
		pluginManager.registerCommand(plugin, new BuildResourcePackCommand());

		pluginManager.registerCommand(plugin, new BuildDatabaseCommand());
		pluginManager.registerCommand(plugin, new QueryCommand());
		pluginManager.registerCommand(plugin, new ExecuteQueryCommand());
		pluginManager.registerCommand(plugin, new UpdateQueryCommand());
		pluginManager.registerCommand(plugin, new BatchQueryCommand());
		pluginManager.registerCommand(plugin, new CallQueryCommand());
	}
}
