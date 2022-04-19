package com.pm.aiost;

import java.util.Scanner;

import com.pm.aiost.misc.BungeeConfigManager;
import com.pm.aiost.misc.BungeeLogger;
import com.pm.aiost.misc.command.CommandHelper;
import com.pm.aiost.misc.command.Commands;
import com.pm.aiost.misc.dataAccess.BungeeDatabaseAccess;
import com.pm.aiost.misc.dataAccess.BungeeFileAccess;
import com.pm.aiost.misc.dataAccess.DataAccess;
import com.pm.aiost.misc.database.DatabaseManager;
import com.pm.aiost.misc.log.Logger;
import com.pm.aiost.server.ServerBuilder;
import com.pm.aiost.server.ServerLoader;
import com.pm.aiost.server.http.HttpServer;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class AiostBungee extends Plugin {

	private static AiostBungee plugin;

	CommandHelper ch = new CommandHelper();

	@Override
	public void onEnable() {
		plugin = this;
		Logger.setLogger(new BungeeLogger());
		Logger.log("Initialize Aiost Bungee...");

		linkListener();
		Commands.linkCommands(plugin);
		BungeeConfigManager.init();
		intDatabase();
		ServerLoader.loadConfiguratedServer();
		HttpServer.start();

		Logger.log("Aiost Bungee initialized!");
	}

	@Override
	public void onDisable() {
		Logger.log("Disable Aiost Bungee...");

		ServerBuilder.shutdownLocalServers();
		HttpServer.stop();
		BungeeConfigManager.terminate();

		Logger.log("Aiost Bungee disabled!");
		waitforUserInput();
	}

	private void linkListener() {
		PluginManager pm = getProxy().getPluginManager();
		pm.registerListener(this, new AiostBungeeListener());
	}

	private static void intDatabase() {
		Logger.log("Initialize Database...");

		if (DatabaseManager.initConnection_(BungeeConfigManager.getAiostConfig()))
			DataAccess.init(new BungeeDatabaseAccess());
		else
			DataAccess.init(new BungeeFileAccess());

		Logger.log("Database initialized!");
	}

	private void waitforUserInput() {
		Logger.log("Waiting for user input!");
		try (Scanner scanner = new Scanner(System.in)) {
			scanner.nextLine();
		}
	}

	public static AiostBungee getPlugin() {
		return plugin;
	}

}
