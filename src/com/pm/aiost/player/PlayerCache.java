package com.pm.aiost.player;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.pm.aiost.collection.list.PersistentArrayList;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerCache {

	private static final int START_SIZE = 1000;

	private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();
	private static final Lock READ_LOCK = LOCK.readLock();
	private static final Lock WRITE_LOCK = LOCK.writeLock();

	private static final PersistentArrayList<ServerPlayer> LIST = new PersistentArrayList<ServerPlayer>(START_SIZE);

	private static final Map<ProxiedPlayer, ServerPlayer> PLAYER_MAP = new IdentityHashMap<ProxiedPlayer, ServerPlayer>(
			START_SIZE);

	static void addPlayer(ServerPlayer serverPlayer) {
		WRITE_LOCK.lock();
		try {
			PLAYER_MAP.put(serverPlayer.player, serverPlayer);
			serverPlayer.initBungeeID(LIST.insert(serverPlayer));
		} finally {
			WRITE_LOCK.unlock();
		}
	}

	static ServerPlayer removePlayer(ProxiedPlayer player) {
		WRITE_LOCK.lock();
		try {
			ServerPlayer serverPlayer = PLAYER_MAP.remove(player);
			LIST.remove(serverPlayer.getBungeeID());
			return serverPlayer;
		} finally {
			WRITE_LOCK.unlock();
		}
	}

	public static ServerPlayer getPlayer(int bungeeID) {
		READ_LOCK.lock();
		try {
			return LIST.get(bungeeID);
		} finally {
			READ_LOCK.unlock();
		}
	}

	public static ServerPlayer getPlayer(ProxiedPlayer player) {
		READ_LOCK.lock();
		try {
			return PLAYER_MAP.get(player);
		} finally {
			READ_LOCK.unlock();
		}
	}

	public static int size() {
		READ_LOCK.lock();
		try {
			return LIST.size();
		} finally {
			READ_LOCK.unlock();
		}
	}

	public static ServerPlayer getPlayer(UUID uuid) {
		return getPlayer(BungeeCord.getInstance().getPlayer(uuid));
	}

	public static ServerPlayer getPlayerPerOfflineUUID(UUID uuid) {
		return getPlayer(BungeeCord.getInstance().getPlayerByOfflineUUID(uuid));
	}

	public static ServerPlayer getPlayer(String name) {
		return getPlayer(BungeeCord.getInstance().getPlayer(name));
	}
}
