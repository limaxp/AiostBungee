package com.pm.aiost.server;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.pm.aiost.misc.ConfigManager;
import com.pm.aiost.misc.log.Logger;
import com.pm.aiost.misc.utils.FileUtils;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ServerBuilder {

	private static String templatePath;
	private static File templateFile;

	private static final Map<Server, LocalServer> LOCAL_SERVER_MAP = new IdentityHashMap<Server, LocalServer>();

	static {
		initTemplate();
	}

	public static void shutdownLocalServers() {
		for (LocalServer localServer : LOCAL_SERVER_MAP.values())
			localServer.process.destroyForcibly();
	}

	private static void initTemplate() {
		templatePath = ConfigManager.getAiostFolderPath() + File.separator + "template";
		templateFile = new File(templatePath);
		if (!templateFile.exists())
			templateFile.mkdir();
		if (!(templateFile.length() > 0))
			Logger.log("ServerBuilder: Template is empty!");
	}

	public static boolean copyTemplate(File target) {
		if (target.exists()) {
			Logger.log("ServerBuilder: Template copy canceled! File '" + target.getAbsolutePath()
					+ "' does already exist!");
			return false;
		}
		if (!(templateFile.length() > 0)) {
			// TODO check if this size check works!
			Logger.log("ServerBuilder: Template copy canceled! Template is empty!");
			return false;
		}
		try {
			FileUtils.copyDirectory(templateFile, target);
			return true;
		} catch (IOException e) {
			Logger.err("ServerBuilder: Error! Template copy failed with file '" + target.getAbsolutePath() + "'", e);
			return false;
		}
	}

	public static void copyConfigToTemplate(File file) {
		if (!(templateFile.length() > 0))
			return;
		File aiostPluginFolder = new File(
				ServerBuilder.getTemplatePath() + File.separator + "plugins" + File.separator + "aiost");
		if (!aiostPluginFolder.exists())
			aiostPluginFolder.mkdir();
		File target = new File(aiostPluginFolder, file.getName());
		try {
			FileUtils.copy(file, target);
		} catch (IOException e) {
			Logger.err("ServerBuilder: Error! Couldn't copy file '" + file.getName() + "' to template!", e);
		}
	}

	public static LocalServer startServer(Server server, File target) {
		if (!target.exists()) {
			Logger.log("ServerBuilder: No server at " + target.getAbsolutePath() + "! Starting template copy!");
			createServer(server, target);
		}
		Process process = startServer(target, server.getType().ram);
		if (process == null)
			return null;
		LocalServer localServer = new LocalServer(process, target);
		LOCAL_SERVER_MAP.put(server, localServer);
		return localServer;
	}

	public static LocalServer stopServer(Server server) {
		LocalServer localServer = LOCAL_SERVER_MAP.remove(server);
		if (localServer == null) {
			Logger.log("ServerBuilder: Server  '" + server.info.getName() + "' cannot be stopped! Must be local!");
			return null;
		}
		localServer.process.destroyForcibly();
		return localServer;
	}

	public static void createServer(Server server, File target) {
		if (!copyTemplate(target))
			return;
		writeServerProperties(server, target);
		writeServerInfoData(server, target);
	}

	public static void deleteServer(Server server) {
		LocalServer localServer = stopServer(server);
		if (localServer != null) {
			try {
				FileUtils.delete(localServer.file);
			} catch (IOException e) {
				Logger.err("ServerBuilder: Error! Server file '" + localServer.file.getAbsolutePath()
						+ "' deletion failed!", e);
			}
		}
	}

	private static void writeServerProperties(Server server, File target) {
		File serverPropFile = new File(target, "server.properties");
		if (serverPropFile.exists()) {
			Path path = serverPropFile.toPath();
			ServerInfo serverInfo = server.info;
			ServerType serverType = server.getType();
			try {
				List<String> fileContent = new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
				for (int i = 0; i < fileContent.size(); i++) {
					String line = fileContent.get(i);
					if (line.startsWith("server-port"))
						fileContent.set(i, "server-port=" + serverInfo.getAddress().getPort());
					if (line.startsWith("motd"))
						fileContent.set(i, "motd=" + serverInfo.getMotd());
					if (line.startsWith("max-players"))
						fileContent.set(i, "max-players=100");
					if (line.startsWith("difficulty"))
						fileContent.set(i, "difficulty=" + serverType.difficulty);
					if (line.startsWith("gamemode"))
						fileContent.set(i, "gamemode=" + serverType.gameMode);
					if (line.startsWith("announce-player-achievements"))
						fileContent.set(i, "announce-player-achievements=" + serverType.announcePlayerAchievement);
					if (line.startsWith("spawn-npcs"))
						fileContent.set(i, "spawn-npcs=" + serverType.spawnNPC);
					if (line.startsWith("spawn-animals"))
						fileContent.set(i, "spawn-animals=" + serverType.spawnAnimals);
					if (line.startsWith("spawn-monsters"))
						fileContent.set(i, "spawn-monsters=" + serverType.spawnMonster);
					if (line.startsWith("max-world-size"))
						fileContent.set(i, "max-world-size=" + serverType.maxWorldSize);
					if (line.startsWith("online-mode"))
						fileContent.set(i, "online-mode=false");
				}
				Files.write(path, fileContent, StandardCharsets.UTF_8);
			} catch (IOException e) {
				Logger.err(
						"ServerBuilder: Error! Server '" + serverInfo.getName() + "' writing server properties failed!",
						e);
			}
		}
	}

	private static void writeServerInfoData(Server server, File target) {
		File aiostFolder = new File(target, "plugins" + File.separator + "aiost");
		if (!aiostFolder.exists())
			aiostFolder.mkdir();
		File serverInfoFile = new File(aiostFolder, "server.dat");
		try {
			if (!serverInfoFile.exists())
				serverInfoFile.createNewFile();
			Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(serverInfoFile);
			config.set("id", server.getId());
			config.set("type", server.getType().toString().toLowerCase());
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, serverInfoFile);
		} catch (IOException e) {
			Logger.err("ServerBuilder: Error! Server '" + server.info.getName() + "' writing server info failed!", e);
		}
	}

	private static Process startServer(File targetFile, int ram) {
		try {
//			return Runtime.getRuntime().exec("java -Xms" + ram + "M -Xmx" + ram
//					+ "M -XX:+AlwaysPreTouch -XX:+DisableExplicitGC -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:MaxGCPauseMillis=45 -XX:TargetSurvivorRatio=90 -XX:G1NewSizePercent=50 -XX:G1MaxNewSizePercent=80 -XX:InitiatingHeapOccupancyPercent=10 -XX:G1MixedGCLiveThresholdPercent=50 -XX:+AggressiveOpts -jar spigot-1.14.2.jar",
//					null, targetFile);
			return Runtime.getRuntime().exec("cmd.exe /c cd \"" + targetFile.getAbsolutePath() + "\" & start java -Xms"
					+ ram + "M -Xmx" + ram
					+ "M -XX:+AlwaysPreTouch -XX:+DisableExplicitGC -XX:+UseG1GC -XX:+UnlockExperimentalVMOptions -XX:MaxGCPauseMillis=45 -XX:TargetSurvivorRatio=90 -XX:G1NewSizePercent=50 -XX:G1MaxNewSizePercent=80 -XX:InitiatingHeapOccupancyPercent=10 -XX:G1MixedGCLiveThresholdPercent=50 -XX:+AggressiveOpts -jar spigot-1.14.2.jar");
		} catch (Exception e) {
			Logger.err("ServerBuilder: Error! Server at '" + targetFile.getAbsolutePath() + "' could not be started!",
					e);
		}
		return null;
	}

	public static File getTemplateFile() {
		return templateFile;
	}

	public static String getTemplatePath() {
		return templatePath;
	}

	public static class LocalServer {

		public final Process process;
		public final File file;

		private LocalServer(Process process, File file) {
			this.process = process;
			this.file = file;
		}
	}
}
