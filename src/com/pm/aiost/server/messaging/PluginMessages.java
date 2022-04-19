package com.pm.aiost.server.messaging;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import com.google.common.io.ByteArrayDataInput;
import com.pm.aiost.game.Game;
import com.pm.aiost.game.GameManager;
import com.pm.aiost.player.PartyManager;
import com.pm.aiost.player.PlayerManager;
import com.pm.aiost.player.ServerPlayer;
import com.pm.aiost.server.Server;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;

public class PluginMessages {

	public static final BiConsumer<PluginMessageEvent, ByteArrayDataInput> NULL_CALLBACK = new BiConsumer<PluginMessageEvent, ByteArrayDataInput>() {
		@Override
		public void accept(PluginMessageEvent arg0, ByteArrayDataInput arg1) {
		}
	};

	private static final Map<String, BiConsumer<PluginMessageEvent, ByteArrayDataInput>> CALLBACKS = new HashMap<String, BiConsumer<PluginMessageEvent, ByteArrayDataInput>>();

	static {
		register("connect",
				(event, in) -> ServerPlayer.getById(in.readInt()).connect(Server.getById(in.readInt(), in.readInt())));

		register("connectAll", (event, in) -> {
			Server server = Server.getById(in.readInt(), in.readInt());
			int size = in.readInt();
			for (int i = 0; i < size; i++)
				ServerPlayer.getById(in.readInt()).connect(server);
		});

		register("connectOpen",
				(event, in) -> ServerPlayer.getById(in.readInt()).connect(Server.getOpen(in.readInt())));

		register("connectAllOpen", (event, in) -> {
			int type = in.readInt();
			int size = in.readInt();
			for (int i = 0; i < size; i++)
				ServerPlayer.getById(in.readInt()).connect(Server.getOpen(type));
		});

		register("connectPerName",
				(event, in) -> ServerPlayer.getById(in.readInt()).connect(Server.getByName(in.readUTF())));

		register("connectAllPerName", (event, in) -> {
			Server server = Server.getByName(in.readUTF());
			int size = in.readInt();
			for (int i = 0; i < size; i++)
				ServerPlayer.getById(in.readInt()).connect(server);
		});

		register("connectPerPlayer", (event, in) -> {
			ProxiedPlayer player = PlayerManager.getProxiedPlayer(in.readUTF());
			if (player != null)
				ServerPlayer.getById(in.readInt()).connect(player.getServer().getInfo());
		});

		register("connectAllPerPlayer", (event, in) -> {
			ProxiedPlayer player = PlayerManager.getProxiedPlayer(in.readUTF());
			if (player != null) {
				ServerInfo server = player.getServer().getInfo();
				int size = in.readInt();
				for (int i = 0; i < size; i++)
					ServerPlayer.getById(in.readInt()).connect(server);
			}
		});

		register("requestPlayerData",
				(event, in) -> PlayerManager.sendPlayerData(ServerPlayer.getByUUID(UUID.fromString(in.readUTF()))));

		register("requestData", (event, in) -> ServerDataProvider.requestData(event.getSender(), in));

		register("hostGame", (event, in) -> GameManager.hostGame(in));

		register("joinGame", (event, in) -> GameManager.joinGame(ServerPlayer.getById(in.readInt()),
				Game.getById(in.readInt(), in.readInt())));

		register("inviteParty",
				(event, in) -> PartyManager.inviteParty(ServerPlayer.getById(in.readInt()), in.readUTF()));

		register("joinParty", (event, in) -> PartyManager.joinParty(ServerPlayer.getById(in.readInt()), in.readUTF()));

		register("leaveParty", (event, in) -> PartyManager.leaveParty(ServerPlayer.getById(in.readInt())));

		register("requestPartyData", (event, in) -> PartyManager.requestPartyData(ServerPlayer.getById(in.readInt())));

		register("removeFromParty", (event, in) -> PartyManager.removeFromParty(ServerPlayer.getById(in.readInt()),
				UUID.fromString(in.readUTF())));
	}

	public static void register(String cmd, BiConsumer<PluginMessageEvent, ByteArrayDataInput> callback) {
		CALLBACKS.put(cmd, callback);
	}

	public static void unregister(String cmd) {
		CALLBACKS.remove(cmd);
	}

	public static BiConsumer<PluginMessageEvent, ByteArrayDataInput> get(String cmd) {
		return CALLBACKS.getOrDefault(cmd, NULL_CALLBACK);
	}

	public static BiConsumer<PluginMessageEvent, ByteArrayDataInput> getOrDefault(String cmd,
			BiConsumer<PluginMessageEvent, ByteArrayDataInput> defaultValue) {
		return CALLBACKS.getOrDefault(cmd, defaultValue);
	}
}