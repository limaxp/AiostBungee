package com.pm.aiost.player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.pm.aiost.misc.dataAccess.DataAccess;
import com.pm.aiost.misc.log.Logger;
import com.pm.aiost.misc.rank.Ranks;
import com.pm.aiost.server.messaging.PluginMessage;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerManager {

	public static ServerPlayer createPlayer(ProxiedPlayer player) {
		ServerPlayer serverPlayer = loadPlayer(player);
		PlayerCache.addPlayer(serverPlayer);
		return serverPlayer;
	}

	public static ServerPlayer removePlayer(ProxiedPlayer player) {
		return PlayerCache.removePlayer(player);
	}

	private static ServerPlayer loadPlayer(ProxiedPlayer player) {
		ResultSet resultSet = null;
		try {
			resultSet = DataAccess.getAccess().getPlayerCore(player.getUniqueId(), player.getName());
			if (resultSet.next()) {
				return new ServerPlayer(player, resultSet.getLong(1), Ranks.get(resultSet.getInt(2)));
			} else
				Logger.warn("Warning: Could not fetch you account data for player " + player.getName());
		} catch (SQLException e) {
			Logger.err("PlayerManager: Error! Could not load data for player " + player.getName(), e);
		} finally {
			DataAccess.getAccess().closeResult(resultSet);
		}
		return null;
	}

	public static void sendPlayerData(ServerPlayer serverPlayer) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("recievePlayerData");
		out.writeUTF(serverPlayer.player.getUniqueId().toString());
		out.writeInt(serverPlayer.getBungeeID());
		out.writeLong(serverPlayer.getDatabaseID());
		PluginMessage.send(serverPlayer.getServer(), out.toByteArray());
	}

	public static ProxiedPlayer getProxiedPlayer(UUID uuid) {
		return BungeeCord.getInstance().getPlayer(uuid);
	}

	public static ProxiedPlayer getProxiedPlayerPerOfflineUUID(UUID uuid) {
		return BungeeCord.getInstance().getPlayerByOfflineUUID(uuid);
	}

	public static ProxiedPlayer getProxiedPlayer(String name) {
		return BungeeCord.getInstance().getPlayer(name);
	}

	public static Collection<ProxiedPlayer> getProxiedPlayers() {
		return BungeeCord.getInstance().getPlayers();
	}
}
