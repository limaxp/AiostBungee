package com.pm.aiost.server;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import com.pm.aiost.collection.list.PersistentArrayList;
import com.pm.aiost.collection.list.UnorderedIdentityArrayList;

import net.md_5.bungee.api.config.ServerInfo;

@SuppressWarnings("unchecked")
public class ServerCache {

	private static final int START_SIZE = 10;

	private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();
	private static final Lock READ_LOCK = LOCK.readLock();
	private static final Lock WRITE_LOCK = LOCK.writeLock();

	private static final Map<ServerInfo, Server> INFO_MAP = new HashMap<ServerInfo, Server>();

	private static final PersistentArrayList<Server>[] TYPE_LISTS;

	private static final List<Server>[] OPEN_TYPE_LISTS;

	static {
		int size = ServerType.size();
		TYPE_LISTS = new PersistentArrayList[size];
		OPEN_TYPE_LISTS = new List[size];

		for (int i = 0; i < size; i++) {
			TYPE_LISTS[i] = new PersistentArrayList<Server>(START_SIZE);
			OPEN_TYPE_LISTS[i] = new UnorderedIdentityArrayList<Server>(START_SIZE);
		}
	}

	static void addServer(Server server) {
		WRITE_LOCK.lock();
		try {
			INFO_MAP.put(server.info, server);
			int type = server.getType().index;
			server.initId(TYPE_LISTS[type].insert(server));
			OPEN_TYPE_LISTS[type].add(server);
		} finally {
			WRITE_LOCK.unlock();
		}
	}

	static void removeServer(Server server) {
		WRITE_LOCK.lock();
		try {
			INFO_MAP.remove(server.info);
			int type = server.getType().index;
			TYPE_LISTS[type].remove(server.getId());
			OPEN_TYPE_LISTS[type].remove(server);
		} finally {
			WRITE_LOCK.unlock();
		}
	}

	static void addOpenServer(Server server) {
		WRITE_LOCK.lock();
		try {
			OPEN_TYPE_LISTS[server.getType().index].add(server);
		} finally {
			WRITE_LOCK.unlock();
		}
	}

	static void removeOpenServer(Server server) {
		WRITE_LOCK.lock();
		try {
			OPEN_TYPE_LISTS[server.getType().index].remove(server);
		} finally {
			WRITE_LOCK.unlock();
		}
	}

	public static Server getServer(ServerInfo info) {
		READ_LOCK.lock();
		try {
			return INFO_MAP.get(info);
		} finally {
			READ_LOCK.unlock();
		}
	}

	public static Server getServer(int type, int id) {
		READ_LOCK.lock();
		try {
			return TYPE_LISTS[type].get(id);
		} finally {
			READ_LOCK.unlock();
		}
	}

	public static Server getServer(ServerType type, int id) {
		return getServer(type.index, id);
	}

	public static Server getOpenServer(int type) {
		READ_LOCK.lock();
		try {
			return OPEN_TYPE_LISTS[type].get(0);
		} finally {
			READ_LOCK.unlock();
		}
	}

	public static Server getOpenServer(ServerType type) {
		return getOpenServer(type.index);
	}

	public static Server requestOpenServer(int type, int playerSize) {
		READ_LOCK.lock();
		try {
			List<Server> typeList = OPEN_TYPE_LISTS[type];
			int size = typeList.size();
			for (int i = 0; i < size; i++) {
				Server server = typeList.get(i);
				if (server.getFreeSlots() >= playerSize) {
					server.addOccupiedSlots(playerSize);
					return server;
				}
			}
		} finally {
			READ_LOCK.unlock();
		}
		return ServerLoader.requestUnusedServer(type, playerSize);
	}

	public static Server requestOpenServer(ServerType type, int playerSize) {
		return requestOpenServer(type.index, playerSize);
	}

	public static void freeOpenServer(Server server, int playerSize) {
		server.removeOccupiedSlots(playerSize);
	}

	public static void forEach(int type, Consumer<Server> consumer) {
		READ_LOCK.lock();
		try {
			List<Server> list = TYPE_LISTS[type];
			int size = list.size();
			for (int i = 0; i < size; i++)
				consumer.accept(list.get(i));
		} finally {
			READ_LOCK.unlock();
		}
	}

	public static void forEach(ServerType type, Consumer<Server> consumer) {
		forEach(type.index, consumer);
	}

	public static void forEachOpen(int type, Consumer<Server> consumer) {
		READ_LOCK.lock();
		try {
			List<Server> list = OPEN_TYPE_LISTS[type];
			int size = list.size();
			for (int i = 0; i < size; i++)
				consumer.accept(list.get(i));
		} finally {
			READ_LOCK.unlock();
		}
	}

	public static void forEachOpen(ServerType type, Consumer<Server> consumer) {
		forEachOpen(type.index, consumer);
	}

	public static int size(int type) {
		READ_LOCK.lock();
		try {
			return TYPE_LISTS[type].size();
		} finally {
			READ_LOCK.unlock();
		}
	}

	public static int size(ServerType type) {
		return size(type.index);
	}

	public static int openSize(int type) {
		READ_LOCK.lock();
		try {
			return OPEN_TYPE_LISTS[type].size();
		} finally {
			READ_LOCK.unlock();
		}
	}

	public static int openSize(ServerType type) {
		return openSize(type.index);
	}
}
