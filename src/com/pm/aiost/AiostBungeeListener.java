package com.pm.aiost;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.pm.aiost.player.PlayerManager;
import com.pm.aiost.player.ServerPlayer;
import com.pm.aiost.server.ServerManager;
import com.pm.aiost.server.messaging.PluginMessage;
import com.pm.aiost.server.messaging.PluginMessages;

import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PermissionCheckEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.event.TabCompleteResponseEvent;
import net.md_5.bungee.api.event.TargetedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class AiostBungeeListener implements Listener {

	@EventHandler
	public void onChat(ChatEvent event) {
	}

	@EventHandler
	public void onPreLogin(PreLoginEvent event) {
	}

	@EventHandler
	public void onLogin(LoginEvent event) {
	}

	@EventHandler
	public void onPostLogin(PostLoginEvent event) {
		PlayerManager.createPlayer(event.getPlayer());
	}

	@EventHandler
	public void onPlayerDisconnect(PlayerDisconnectEvent event) {
		ServerManager.onPlayerDisconnectServer(PlayerManager.removePlayer(event.getPlayer()));
	}

	@EventHandler
	public void onPlayerHandshake(PlayerHandshakeEvent event) {
	}

	@EventHandler
	public void onPermissionCheck(PermissionCheckEvent event) {
	}

	@EventHandler
	public void onProxyPing(ProxyPingEvent event) {
	}

	@EventHandler
	public void onProxyReload(ProxyReloadEvent event) {
	}

	@EventHandler
	public void onPluginMessage(PluginMessageEvent event) {
		if (event.getTag().equals(PluginMessage.CHANNEL_KEY)) {
			ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
			PluginMessages.get(in.readUTF()).accept(event, in);
		}
	}

	@EventHandler
	public void onServerConnect(ServerConnectEvent event) {
		if (event.isCancelled())
			return;
		ServerManager.onPlayerConnectServer(ServerPlayer.getByPlayer(event.getPlayer()), event);
	}

	@EventHandler
	public void onServerConnected(ServerConnectedEvent event) {
		ServerManager.onPlayerConnectedServer(ServerPlayer.getByPlayer(event.getPlayer()));
	}

	@EventHandler
	public void onServerDisconnect(ServerDisconnectEvent event) {
		ServerPlayer serverPlayer = ServerPlayer.getByPlayer(event.getPlayer());
		if (serverPlayer != null) // == null if player quit
			ServerManager.onPlayerDisconnectServer(serverPlayer);
	}

	@EventHandler
	public void onServerSwitch(ServerSwitchEvent event) {
	}

	@EventHandler
	public void onServerKick(ServerKickEvent event) {
	}

	@EventHandler
	public void onTabComplete(TabCompleteEvent event) {
	}

	@EventHandler
	public void onTabCompleteResponse(TabCompleteResponseEvent event) {
	}

	@EventHandler
	public void onTargeted(TargetedEvent event) {
	}
}
