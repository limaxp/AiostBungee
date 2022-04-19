package com.pm.aiost.player;

import java.util.List;
import java.util.UUID;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.pm.aiost.server.messaging.PluginMessage;

public class PartyManager {

	public static void inviteParty(ServerPlayer serverPlayer, String playerName) {
		ServerPlayer targetServerPlayer = ServerPlayer.getByName(playerName);
		if (targetServerPlayer == null) {
			serverPlayer.sendError("No player found for name '" + playerName + "'");
			return;
		}
		Party party = serverPlayer.getParty();
		if (!party.isOwner(serverPlayer)) {
			serverPlayer.sendError("Only party owner can invite other player!");
			return;
		}
		party.addInvited(targetServerPlayer);
		sendPartyRequest(targetServerPlayer, serverPlayer.player.getName());
		serverPlayer.sendMessage("Party invitation was sent to player '" + playerName + "'");
	}

	public static void joinParty(ServerPlayer serverPlayer, String requestPlayerName) {
		ServerPlayer requestServerPlayer = ServerPlayer.getByName(requestPlayerName);
		if (requestServerPlayer == null) {
			serverPlayer.sendError("No player found for name '" + requestPlayerName + "'");
			return;
		}
		Party party = requestServerPlayer.getParty();
		if (!party.isInvited(serverPlayer)) {
			serverPlayer.sendError("You don't have an invitation or it has run out!");
			return;
		}
		serverPlayer.joinParty(party);
		serverPlayer.sendMessage("Accepted party invitation from '" + requestPlayerName + "'");
	}

	public static void leaveParty(ServerPlayer serverPlayer) {
		if (!serverPlayer.hasParty()) {
			serverPlayer.sendError("Not currently in a party!");
			return;
		}
		serverPlayer.leaveParty();
		serverPlayer.sendMessage("Party left!");
	}

	public static void requestPartyData(ServerPlayer serverPlayer) {
		if (!serverPlayer.hasParty())
			return;
		List<ServerPlayer> partyMember = serverPlayer.getParty().getMember();
		int size = partyMember.size();
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("recievePartyData");
		out.writeInt(serverPlayer.getBungeeID());
		out.writeInt(size);
		for (int i = 0; i < size; i++)
			out.writeUTF(partyMember.get(i).player.getUniqueId().toString());
		PluginMessage.send(serverPlayer.getServer(), out.toByteArray());
	}

	public static void removeFromParty(ServerPlayer serverPlayer, UUID uuid) {
		ServerPlayer targetServerPlayer = ServerPlayer.getByUUID(uuid);
		if (targetServerPlayer == null) {
			serverPlayer.sendError("No player found for uuid '" + uuid.toString() + "'");
			return;
		}
		Party party = serverPlayer.getParty();
		if (!party.isOwner(serverPlayer)) {
			serverPlayer.sendError("Only party owner can remove player!");
			return;
		}
		if (!party.contains(targetServerPlayer)) {
			serverPlayer.sendError("party does not contain this player!");
			return;
		}
		targetServerPlayer.leaveParty();
		requestPartyData(serverPlayer);
	}

	private static void sendPartyRequest(ServerPlayer serverPlayer, String requestPlayerName) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("partyRequest");
		out.writeInt(serverPlayer.getBungeeID());
		out.writeUTF(requestPlayerName);
		PluginMessage.send(serverPlayer.getServer(), out.toByteArray());
	}
}
