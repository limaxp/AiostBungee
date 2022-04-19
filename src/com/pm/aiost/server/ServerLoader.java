package com.pm.aiost.server;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.pm.aiost.misc.BungeeConfigManager;
import com.pm.aiost.server.messaging.PluginMessage;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class ServerLoader {

	private static final ProxyServer PROXY_SERVER = ProxyServer.getInstance();

	private static final BlockingQueue<Server> UNUSED_SERVER_LIST = new LinkedBlockingQueue<Server>();

	public static void loadConfiguratedServer() {
		Configuration serversConfig = BungeeConfigManager.getBungeeCordServerSection();
		for (String serverName : serversConfig.getKeys()) {
			Configuration serverConfig = serversConfig.getSection(serverName);
			String typeName = serverConfig.getString("type");
			ServerType serverType;
			if (typeName.isEmpty())
				serverType = ServerType.NONE;
			else
				serverType = ServerType.getIgnoreCase(typeName);
			initServer(BungeeCord.getInstance().getServerInfo(serverName), serverType);
		}
	}

	public static Server registerServer(String name, ServerType serverType, String motd, boolean restricted,
			InetSocketAddress address) {
		ServerInfo serverInfo = PROXY_SERVER.constructServerInfo(name, address, motd, restricted);
		PROXY_SERVER.getServers().put(name, serverInfo);
		return registerServer(serverInfo, serverType);
	}

	public static Server registerServer(ServerInfo serverInfo, ServerType serverType) {
		Server server = initServer(serverInfo, serverType);
		addServerInConfig(server);
		return server;
	}

	private static Server initServer(ServerInfo serverInfo, ServerType serverType) {
		Server server = new Server(serverInfo, serverType);
		if (serverType == ServerType.NONE)
			UNUSED_SERVER_LIST.add(server);
		else {
			ServerCache.addServer(server);
			sendServerType(server);
		}
		return server;
	}

	public static void unregisterServer(Server server) {
		ServerInfo serverInfo = server.info;
		String serverName = serverInfo.getName();
		ServerCache.removeServer(server);
		PROXY_SERVER.getServers().remove(serverInfo.getName());
		removeServerFromConfig(serverName);
		TextComponent disconnectText = new TextComponent(
				"This server was forcefully closed.\nPlease report this error in the bug report section of the forums.");
		for (ProxiedPlayer p : serverInfo.getPlayers())
			p.disconnect(disconnectText);
	}

	private static void addServerInConfig(Server server) {
		Configuration serverSection = BungeeConfigManager.getBungeeCordServerSection();
		ServerInfo serverInfo = server.info;
		Configuration currentServer = serverSection.getSection(serverInfo.getName());
		if (currentServer == null)
			serverSection.set(serverInfo.getName(), currentServer = new Configuration());
		serverSection.set("motd", serverInfo.getName());
		serverSection.set("address", serverInfo.getAddress());
		serverSection.set("restricted", false);
		serverSection.set("type", server.getType().toString().toLowerCase());
	}

	private static void removeServerFromConfig(String name) {
		Configuration serverSection = BungeeConfigManager.getBungeeCordServerSection();
		if (serverSection.contains(name))
			serverSection.set(name, null);
	}

	private static void sendServerType(Server server) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("serverType");
		out.writeInt(server.getType().index);
		PluginMessage.send(server, out.toByteArray());
	}

	static Server requestUnusedServer(int type, int playerSize) {
		return requestUnusedServer(ServerType.get(type), playerSize);
	}

	static Server requestUnusedServer(ServerType type, int playerSize) {
		Server server = UNUSED_SERVER_LIST.poll();
		if (server == null)
			return null;
		server.setType(type);
		server.addOccupiedSlots(playerSize);
		ServerCache.addServer(server);
		sendServerType(server);
		return server;
	}
}
