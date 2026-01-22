package com.pm.aiost.misc;

import java.io.File;
import java.io.IOException;

import com.pm.aiost.misc.log.Logger;
import com.pm.aiost.misc.rank.Ranks;
import com.pm.aiost.misc.resourcePack.ResourcePackBuilder;
import com.pm.aiost.misc.utils.FileUtils;
import com.pm.aiost.server.ServerBuilder;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class BungeeConfigManager extends ConfigManager {

	private static final ConfigurationProvider PROVIDER = ConfigurationProvider.getProvider(YamlConfiguration.class);

	private static String bungeeCordPath;
	private static Configuration bungeeCordConfig;
	private static Configuration aiostConfig;

	static {
		File pluginsFile = ProxyServer.getInstance().getPluginsFolder();
		bungeeCordPath = pluginsFile.getAbsolutePath();
		bungeeCordPath = bungeeCordPath.substring(0, bungeeCordPath.lastIndexOf(File.separator));
		initAiostFolderPath(pluginsFile + File.separator + "aiost");
	}

	public static void init() {
		Logger.log("Initialize ConfigManager...");

		bungeeCordConfig = loadConfig(new File(bungeeCordPath, "config.yml"));

		File aiostFile = initResource("Aiost.yml");
		aiostConfig = initConfig(aiostFile);
		Ranks.register_(aiostConfig.getSection("ranks"));
		ServerBuilder.copyConfigToTemplate(aiostFile);

		File itemsFile = initResource("Items.yml");
		ServerBuilder.copyConfigToTemplate(itemsFile);
		ResourcePackBuilder.checkResourcePack_(loadConfig(itemsFile));

		File mobsFile = initResource("Mobs.yml");
		ServerBuilder.copyConfigToTemplate(mobsFile);

		File recipesFile = initResource("Recipes.yml");
		ServerBuilder.copyConfigToTemplate(recipesFile);

		File effectGroupsFile = initResource("Effects.yml");
		ServerBuilder.copyConfigToTemplate(effectGroupsFile);

		File unlockablesFile = initResource("Unlockables.yml");
		ServerBuilder.copyConfigToTemplate(unlockablesFile);

		File particlesFile = initResource("Particles.yml");
		ServerBuilder.copyConfigToTemplate(particlesFile);

		Logger.log("ConfigManager initialized!");
	}

	public static void terminate() {
		Logger.log("Terminate ConfigManager...");

		saveConfig(bungeeCordConfig, new File(bungeeCordPath, "config.yml"));
		String configFolderPath = getConfigFolderPath();
		saveConfig(aiostConfig, new File(configFolderPath, "Aiost.yml"));

		Logger.log("ConfigManager terminated!");
	}

	private static Configuration initConfig(File file) {
		FileUtils.createNotExisting_(file);
		return loadConfig(file);
	}

	public static Configuration loadConfig(File file) {
		Logger.log("ConfigManager: Loading file '" + file.getName() + "'");
		try {
			return PROVIDER.load(file);
		} catch (IOException e) {
			Logger.err("ConfigManager: Couldn't load file '" + file.getName() + "'", e);
		}
		return null;
	}

	private static void saveConfig(Configuration config, File file) {
		Logger.log("ConfigManager: Saving file '" + file.getName() + "'");
		try {
			PROVIDER.save(config, file);
		} catch (IOException e) {
			Logger.err("ConfigManager: Couldn't save " + file.getName() + "!", e);
		}
	}

	public static String getBungeeCordPath() {
		return bungeeCordPath;
	}

	public static Configuration getBungeeCordConfig() {
		return bungeeCordConfig;
	}

	public static Configuration getBungeeCordServerSection() {
		return bungeeCordConfig.getSection("servers");
	}

	public static Configuration getAiostConfig() {
		return aiostConfig;
	}
}
