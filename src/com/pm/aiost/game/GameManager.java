package com.pm.aiost.game;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.pm.aiost.AiostBungee;
import com.pm.aiost.player.Party;
import com.pm.aiost.player.ServerPlayer;
import com.pm.aiost.server.Server;
import com.pm.aiost.server.ServerCache;
import com.pm.aiost.server.ServerType;
import com.pm.aiost.server.messaging.PluginMessage;

import net.md_5.bungee.api.ProxyServer;

public class GameManager {

	public static void hostGame(ByteArrayDataInput in) {
		int serverPlayerID = in.readInt();
		ServerPlayer serverPlayer = ServerPlayer.getById(serverPlayerID);
		Game game = new Game(UUID.fromString(in.readUTF()), in.readUTF(), in.readUTF(), in.readInt(), in.readInt(),
				in.readInt(), in.readBoolean(), in.readInt(), in.readInt(), in.readUTF()); // TODO test if password is
																							// empty here if empty in
																							// spigot!
		Server server = ServerCache.requestOpenServer(ServerType.GAME, game.getMaxPlayer());
		if (server == null) {
			serverPlayer.sendError("No free server found!");
			return;
		}
		game.initServer(server);
		serverPlayer.connect(server, (successful, e) -> {
			if (!successful) {
				ServerCache.freeOpenServer(server, game.getMaxPlayer());
				return;
			}
			game.addPlayer(serverPlayer);
			GameCache.addGame(game);
//			PluginMessage.send(server, startGameMessage(serverPlayer, game));
			ProxyServer.getInstance().getScheduler().schedule(AiostBungee.getPlugin(),
					() -> PluginMessage.send(server, startGameMessage(serverPlayer, game)), 1, TimeUnit.SECONDS);
			joinGameParty(serverPlayer, game);
		});
	}

	public static void joinGame(ServerPlayer serverPlayer, Game game) {
		int playerSize = game.getPlayerSize();
		int maxPlayer = game.getMaxPlayer();
		if (playerSize >= maxPlayer) {
			serverPlayer.sendError("Game is full!");
			return;
		}
		if (!joinGameParty(serverPlayer, game))
			joinGameSingle(serverPlayer, game);
	}

	private static void joinGameSingle(ServerPlayer serverPlayer, Game game) {
		Server server = game.getServer();
		serverPlayer.connect(server, (successful, e) -> {
			if (successful) {
				if (game.getPlayerSize() + 1 == game.getMaxPlayer())
					GameCache.removeGame(game);
				game.addPlayer(serverPlayer);
				PluginMessage.send(server, joinGameMessage(serverPlayer, game));
			}
		});
	}

	private static boolean joinGameParty(ServerPlayer serverPlayer, Game game) {
		Party party;
		if (serverPlayer.hasParty() && (party = serverPlayer.getParty()).isOwner(serverPlayer)) {
			for (ServerPlayer partyPlayer : party.getMember())
				if (!partyPlayer.playsGame())
					joinGameSingle(partyPlayer, game);
			return true;
		}
		return false;
	}

	public static void leaveGame(ServerPlayer serverPlayer, Game game) {
		if (game.getPlayerSize() == game.getMaxPlayer()) {
			game.removePlayer(serverPlayer);
			GameCache.addGame(game);
		} else if (game.getPlayerSize() == 1) {
			GameCache.removeGame(game);
			game.removePlayer(serverPlayer);
			ServerCache.freeOpenServer(game.getServer(), game.getMaxPlayer());
		} else
			game.removePlayer(serverPlayer);
	}

	private static byte[] startGameMessage(ServerPlayer serverPlayer, Game game) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("startGame");
		out.writeInt(serverPlayer.getBungeeID());
		out.writeInt(game.getId());
		out.writeUTF(game.getUUID().toString());
		out.writeUTF(game.getName());
		out.writeUTF(game.getOwnerName());
		out.writeInt(game.getGameType());
		out.writeInt(game.getEnvironment());
		out.writeInt(game.getWorldType());
		out.writeBoolean(game.generateStructures());
		out.writeInt(game.getMinPlayer());
		out.writeInt(game.getMaxPlayer());
		return out.toByteArray();
	}

	private static byte[] joinGameMessage(ServerPlayer serverPlayer, Game game) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("joinGame");
		out.writeInt(serverPlayer.getBungeeID());
		out.writeInt(game.getGameType());
		out.writeInt(game.getId());
		return out.toByteArray();
	}
}
