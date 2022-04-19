package com.pm.aiost.server;

import java.util.Map;

import com.pm.aiost.game.GameManager;
import com.pm.aiost.player.PlayerManager;
import com.pm.aiost.player.ServerPlayer;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent.Reason;

public class ServerManager {

	public static void onPlayerConnectServer(ServerPlayer serverPlayer, ServerConnectEvent event) {
		if (event.getReason() == Reason.JOIN_PROXY) {
			Server server = ServerCache.getOpenServer(ServerType.LOBBY);
			serverPlayer.setServer(server);
			if (server.info != event.getTarget())
				event.setTarget(server.info);
		} else
			serverPlayer.setServer(ServerCache.getServer(event.getTarget()));
	}

	public static void onPlayerConnectedServer(ServerPlayer serverPlayer) {
		Server server = serverPlayer.getServer();
		if (server.getState() == ServerState.OPEN) {
			if (server.info.getPlayers().size() >= server.getType().maxPlayer) {
				server.setServerState(ServerState.FULL);
				ServerCache.removeOpenServer(server);
			}
		}
		PlayerManager.sendPlayerData(serverPlayer);
	}

	public static void onPlayerDisconnectServer(ServerPlayer serverPlayer) {
		Server server = serverPlayer.getServer();
		if (server.getState() == ServerState.FULL) {
			server.setServerState(ServerState.OPEN);
			ServerCache.addOpenServer(server);
		}
		if (serverPlayer.playsGame())
			GameManager.leaveGame(serverPlayer, serverPlayer.getGame());
	}

	public static ServerInfo getServerInfo(String name) {
		return BungeeCord.getInstance().getServerInfo(name);
	}

	public static Map<String, ServerInfo> getServerInfos() {
		return BungeeCord.getInstance().getServers();
	}
}
