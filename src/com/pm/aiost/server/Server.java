package com.pm.aiost.server;

import java.util.concurrent.atomic.AtomicInteger;

import net.md_5.bungee.api.config.ServerInfo;

public class Server {

	public final ServerInfo info;
	private int id;
	private ServerType type;
	private ServerState state;
	private AtomicInteger occupiedSlots;

	Server(ServerInfo info, ServerType type) {
		this.info = info;
		this.type = type;
		state = ServerState.OPEN;
		occupiedSlots = new AtomicInteger();
	}

	void initId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	void setType(ServerType type) {
		this.type = type;
	}

	public ServerType getType() {
		return type;
	}

	void setServerState(ServerState serverState) {
		this.state = serverState;
	}

	public ServerState getState() {
		return state;
	}

	int addOccupiedSlots(int size) {
		return occupiedSlots.addAndGet(size);
	}

	int removeOccupiedSlots(int size) {
		return occupiedSlots.addAndGet(-size);
	}

	public int getOccupiedSlots() {
		return occupiedSlots.get();
	}

	public int getFreeSlots() {
		return type.maxPlayer - getOccupiedSlots();
	}

	public static Server getByInfo(ServerInfo info) {
		return ServerCache.getServer(info);
	}

	public static Server getByName(String name) {
		return ServerCache.getServer(ServerManager.getServerInfo(name));
	}

	public static Server getById(ServerType type, int id) {
		return ServerCache.getServer(type, id);
	}

	public static Server getById(int type, int id) {
		return ServerCache.getServer(type, id);
	}

	public static Server getOpen(ServerType type) {
		return ServerCache.getOpenServer(type);
	}

	public static Server getOpen(int type) {
		return ServerCache.getOpenServer(type);
	}
}
