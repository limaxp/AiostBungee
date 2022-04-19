package com.pm.aiost.server.messaging;

import com.google.common.io.ByteArrayDataOutput;
import com.pm.aiost.server.Server;

import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.api.config.ServerInfo;

public class PluginMessage {

	public static final String CHANNEL_KEY = "BungeeCord";

	public static void send(ServerInfo server, byte[] data) {
		server.sendData(CHANNEL_KEY, data);
	}

	public static void send(ServerInfo server, ByteArrayDataOutput data) {
		send(server, data.toByteArray());
	}

	public static void send(Server server, byte[] data) {
		send(server.info, data);
	}

	public static void send(Server server, ByteArrayDataOutput data) {
		send(server.info, data.toByteArray());
	}

	public static void send(net.md_5.bungee.api.connection.Server server, byte[] data) {
		server.sendData(CHANNEL_KEY, data);
	}

	public static void send(net.md_5.bungee.api.connection.Server server, ByteArrayDataOutput data) {
		send(server, data.toByteArray());
	}

	public static void send(ServerConnection server, byte[] data) {
		server.sendData(CHANNEL_KEY, data);
	}

	public static void send(ServerConnection server, ByteArrayDataOutput data) {
		send(server, data.toByteArray());
	}

	public static boolean send(ServerInfo server, byte[] data, boolean queue) {
		return server.sendData(CHANNEL_KEY, data, queue);
	}

	public static boolean send(ServerInfo server, ByteArrayDataOutput data, boolean queue) {
		return send(server, data.toByteArray(), queue);
	}

	public static boolean send(Server server, byte[] data, boolean queue) {
		return send(server.info, data, queue);
	}

	public static boolean send(Server server, ByteArrayDataOutput data, boolean queue) {
		return send(server.info, data.toByteArray(), queue);
	}

	public static boolean send(net.md_5.bungee.api.connection.Server server, byte[] data, boolean queue) {
		return send(server.getInfo(), data, queue);
	}

	public static boolean send(net.md_5.bungee.api.connection.Server server, ByteArrayDataOutput data, boolean queue) {
		return send(server.getInfo(), data.toByteArray(), queue);
	}

	public static boolean send(ServerConnection server, byte[] data, boolean queue) {
		return send(server.getInfo(), data, queue);
	}

	public static boolean send(ServerConnection server, ByteArrayDataOutput data, boolean queue) {
		return send(server.getInfo(), data.toByteArray(), queue);
	}
}
