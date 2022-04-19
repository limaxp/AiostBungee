package com.pm.aiost.server.messaging;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.pm.aiost.game.GameCache;
import com.pm.aiost.player.PlayerManager;
import com.pm.aiost.server.ServerCache;

import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.api.connection.Connection;

public class ServerDataProvider {

	public static void requestData(Connection sender, ByteArrayDataInput in) {
		if (!(sender instanceof ServerConnection))
			return;
		ServerConnection server = (ServerConnection) sender;
		int serverSize = in.readInt();
		int[] serverTypes = new int[serverSize];
		for (int i = 0; i < serverSize; i++)
			serverTypes[i] = in.readInt();

		int gameSize = in.readInt();
		int[] gameTypes = new int[gameSize];
		for (int i = 0; i < gameSize; i++)
			gameTypes[i] = in.readInt();

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("recieveData");
		out.writeInt(PlayerManager.getProxiedPlayers().size());
		writeServerLists(out, serverTypes);
		writeGameLists(out, gameTypes);
		PluginMessage.send(server, out.toByteArray());
	}

	private static void writeServerLists(ByteArrayDataOutput out, int... types) {
		out.writeInt(types.length);
		for (int i = 0; i < types.length; i++)
			writeServerList(out, types[i]);
	}

	private static void writeServerList(ByteArrayDataOutput out, int type) {
		int size = ServerCache.size(type);
		if (size > 0) {
			out.writeInt(type);
			out.writeInt(size);
			ServerCache.forEach(type, (server) -> {
				out.writeInt(server.getId());
				out.writeUTF(server.info.getName());
				out.writeInt(server.getState().ordinal());
			});
		}
	}

	private static void writeGameLists(ByteArrayDataOutput out, int... types) {
		out.writeInt(types.length);
		for (int i = 0; i < types.length; i++)
			writeGameLists(out, types[i]);
	}

	private static void writeGameLists(ByteArrayDataOutput out, int type) {
		int size = GameCache.size(type);
		if (size > 0) {
			out.writeInt(type);
			out.writeInt(size);
			GameCache.forEach(type, (game) -> {
				out.writeInt(game.getId());
				out.writeUTF(game.getUUID().toString());
				out.writeUTF(game.getName());
				out.writeUTF(game.getOwnerName());
				out.writeInt(game.getPlayerSize());
				out.writeInt(game.getMinPlayer());
				out.writeInt(game.getMaxPlayer());
				out.writeUTF(game.getPassword());
			});
		}
	}
}
