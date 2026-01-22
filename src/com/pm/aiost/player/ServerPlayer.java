package com.pm.aiost.player;

import java.util.UUID;

import com.pm.aiost.game.Game;
import com.pm.aiost.misc.rank.Rank;
import com.pm.aiost.server.Server;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent.Reason;

public class ServerPlayer {

	public final ProxiedPlayer player;
	private int bungeeID;
	private final long databaseID;
	private final Rank rank;
	private Server server;
	private Game game;
	private Party party;

	ServerPlayer(ProxiedPlayer player, long databaseID, Rank rank) {
		this.player = player;
		this.databaseID = databaseID;
		this.rank = rank;
	}

	void initBungeeID(int bungeeID) {
		this.bungeeID = bungeeID;
	}

	public int getBungeeID() {
		return bungeeID;
	}

	public long getDatabaseID() {
		return databaseID;
	}

	public Rank getRank() {
		return rank;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public Server getServer() {
		return server;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

	public boolean playsGame() {
		return game != null;
	}

	public void joinParty(Party party) {
		leaveParty();
		party.addMember(this);
		this.party = party;
	}

	public void leaveParty() {
		if (this.party != null) {
			party.removeMember(this);
			party = null;
		}
	}

	public Party getParty() {
		if (party == null)
			party = new Party(this);
		return party;
	}

	public boolean hasParty() {
		return party != null;
	}

	public void connect(ServerInfo serverInfo) {
		player.connect(serverInfo);
	}

	public void connect(Server server) {
		player.connect(server.info);
	}

	public void connect(ServerInfo serverInfo, Callback<Boolean> callback) {
		player.connect(serverInfo, callback);
	}

	public void connect(Server server, Callback<Boolean> callback) {
		player.connect(server.info, callback);
	}

	public void connect(ServerInfo serverInfo, Callback<Boolean> callback, Reason reason) {
		player.connect(serverInfo, callback, reason);
	}

	public void connect(Server server, Callback<Boolean> callback, Reason reason) {
		player.connect(server.info, callback, reason);
	}

	public void sendMessage(String msg) {
		player.sendMessage(new ComponentBuilder(msg).create());
	}

	public void sendError(String msg) {
		player.sendMessage(new ComponentBuilder(msg).color(ChatColor.RED).create());
	}

	public static ServerPlayer getById(int bungeeID) {
		return PlayerCache.getPlayer(bungeeID);
	}

	public static ServerPlayer getByPlayer(ProxiedPlayer player) {
		return PlayerCache.getPlayer(player);
	}

	public static ServerPlayer getByUUID(UUID uuid) {
		return PlayerCache.getPlayer(uuid);
	}

	public static ServerPlayer getByName(String name) {
		return PlayerCache.getPlayer(name);
	}
}
