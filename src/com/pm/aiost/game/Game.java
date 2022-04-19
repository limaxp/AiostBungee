package com.pm.aiost.game;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.pm.aiost.collection.list.UnorderedIdentityArrayList;
import com.pm.aiost.player.ServerPlayer;
import com.pm.aiost.server.Server;

import jline.internal.Nullable;

public class Game {

	private Server server;
	private int id;
	private final UUID uuid;
	private final String name;
	private final String ownerName;
	private final int gameType;
	private final int environment;
	private final int worldType;
	private final boolean generateStructures;
	private final int minPlayer;
	private final int maxPlayer;
	private final String password;
	private final List<ServerPlayer> player;

	Game(UUID uuid, String name, String ownerName, int gameType, int environment, int worldType,
			boolean generateStructures, int minPlayer, int maxPlayer, @Nullable String password) {
		this.uuid = uuid;
		this.name = name;
		this.ownerName = ownerName;
		this.gameType = gameType;
		this.environment = environment;
		this.worldType = worldType;
		this.generateStructures = generateStructures;
		this.minPlayer = minPlayer;
		this.maxPlayer = maxPlayer;
		this.password = password;
		player = Collections.synchronizedList(new UnorderedIdentityArrayList<ServerPlayer>(maxPlayer));
	}

	void initServer(Server server) {
		this.server = server;
	}

	public Server getServer() {
		return server;
	}

	void initId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public UUID getUUID() {
		return uuid;
	}

	public String getName() {
		return name;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public int getGameType() {
		return gameType;
	}

	public int getEnvironment() {
		return environment;
	}

	public int getWorldType() {
		return worldType;
	}

	public boolean generateStructures() {
		return generateStructures;
	}

	public int getMinPlayer() {
		return minPlayer;
	}

	public int getMaxPlayer() {
		return maxPlayer;
	}

	@Nullable
	public String getPassword() {
		return password;
	}

	void addPlayer(ServerPlayer serverPlayer) {
		serverPlayer.setGame(this);
		player.add(serverPlayer);
	}

	void removePlayer(ServerPlayer serverPlayer) {
		serverPlayer.setGame(null);
		player.remove(serverPlayer);
	}

	public int getPlayerSize() {
		return player.size();
	}

	public void forEach(Consumer<ServerPlayer> consumer) {
		synchronized (player) {
			for (ServerPlayer p : player)
				consumer.accept(p);
		}
	}

	public static Game getById(int type, int id) {
		return GameCache.getGame(type, id);
	}
}
