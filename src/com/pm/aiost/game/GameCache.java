package com.pm.aiost.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import com.pm.aiost.collection.list.PersistentArrayList;
import com.pm.aiost.collection.list.UnorderedIdentityArrayList;

public class GameCache {

	private static final int START_SIZE = 1000;

	private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();
	private static final Lock READ_LOCK = LOCK.readLock();
	private static final Lock WRITE_LOCK = LOCK.writeLock();

	private static final Map<Integer, GameTypeEntry> TYPE_MAP = new HashMap<Integer, GameTypeEntry>();

	static void addGame(Game game) {
		WRITE_LOCK.lock();
		try {
			GameTypeEntry entry = getTypeEntry(game.getGameType());
			game.initId(entry.list.insert(game));
			entry.openList.add(game);
		} finally {
			WRITE_LOCK.unlock();
		}
	}

	static void removeGame(Game game) {
		WRITE_LOCK.lock();
		try {
			GameTypeEntry entry = getTypeEntry(game.getGameType());
			entry.list.remove(game.getId());
			entry.openList.remove(game);
		} finally {
			WRITE_LOCK.unlock();
		}
	}

	public static Game getGame(int type, int id) {
		READ_LOCK.lock();
		try {
			return getTypeEntry(type).list.get(id);
		} finally {
			READ_LOCK.unlock();
		}
	}

	private static GameTypeEntry getTypeEntry(int type) {
		GameTypeEntry entry = TYPE_MAP.get(type);
		if (entry == null)
			TYPE_MAP.put(type, entry = new GameTypeEntry());
		return entry;
	}

	public static void forEach(int type, Consumer<Game> consumer) {
		READ_LOCK.lock();
		try {
			List<Game> list = getTypeEntry(type).openList;
			int size = list.size();
			for (int i = 0; i < size; i++)
				consumer.accept(list.get(i));
		} finally {
			READ_LOCK.unlock();
		}
	}

	public static int size(int type) {
		READ_LOCK.lock();
		try {
			return getTypeEntry(type).openList.size();
		} finally {
			READ_LOCK.unlock();
		}
	}

	private static class GameTypeEntry {

		private final PersistentArrayList<Game> list = new PersistentArrayList<Game>(START_SIZE);
		private final List<Game> openList = new UnorderedIdentityArrayList<Game>(START_SIZE);
	}
}
