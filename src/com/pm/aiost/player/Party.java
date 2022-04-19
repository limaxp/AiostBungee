package com.pm.aiost.player;

import java.util.Collections;
import java.util.List;

import com.pm.aiost.collection.list.UnorderedIdentityArrayList;

public class Party {

	public static final int MAX_PARTY_SIZE = 4;
	public static final int MAX_INVITE_SIZE = 20;

	private final List<ServerPlayer> member;
	private final List<ServerPlayer> invited;
	private int invitedIndex;

	public Party(ServerPlayer owner) {
		member = Collections.synchronizedList(new UnorderedIdentityArrayList<ServerPlayer>(MAX_PARTY_SIZE));
		invited = Collections.synchronizedList(new UnorderedIdentityArrayList<ServerPlayer>(MAX_INVITE_SIZE));
		addMember(owner);
	}

	boolean addMember(ServerPlayer serverPlayer) {
		if (size() >= MAX_PARTY_SIZE)
			return false;
		member.add(serverPlayer);
		return true;
	}

	void removeMember(ServerPlayer serverPlayer) {
		member.remove(serverPlayer);
	}

	void addInvited(ServerPlayer serverPlayer) {
		if (invited.size() >= MAX_INVITE_SIZE) {
			invited.set(invitedIndex++, serverPlayer);
			if (invitedIndex >= MAX_INVITE_SIZE)
				invitedIndex = 0;
			return;
		}
		invited.add(serverPlayer);
	}

	public boolean isInvited(ServerPlayer serverPlayer) {
		return invited.contains(serverPlayer);
	}

	public List<ServerPlayer> getMember() {
		return Collections.unmodifiableList(member);
	}

	public boolean contains(ServerPlayer serverPlayer) {
		return member.contains(serverPlayer);
	}

	public ServerPlayer getOwner() {
		return member.get(0);
	}

	public boolean isOwner(ServerPlayer serverPlayer) {
		return getOwner() == serverPlayer;
	}

	public int size() {
		return member.size();
	}
}
