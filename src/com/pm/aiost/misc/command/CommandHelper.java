package com.pm.aiost.misc.command;

import java.util.Arrays;

import com.pm.aiost.misc.rank.Rank;
import com.pm.aiost.player.ServerPlayer;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;

public class CommandHelper {

	public static void sendMsg(CommandSender sender, String msg) {
		sender.sendMessage(new ComponentBuilder(msg).create());
	}

	public static void sendError(CommandSender sender, String msg) {
		sender.sendMessage(new ComponentBuilder(msg).color(ChatColor.RED).create());
	}

	public static boolean isPlayer(CommandSender sender) {
		if (sender instanceof ProxiedPlayer)
			return true;
		sendError(sender, "This command can only be run by a player!");
		return false;
	}

	public static boolean isConsole(CommandSender sender) {
		if (sender instanceof ConsoleCommandSender)
			return true;
		sendError(sender, "This command can only be run by the console!");
		return false;
	}

	public static boolean isPlayerOrConsole(CommandSender sender) {
		if (sender instanceof ProxiedPlayer || sender instanceof ConsoleCommandSender)
			return true;
		sendError(sender, "This command can be run by either players or the console!");
		return false;
	}

	public static boolean hasGroup(CommandSender sender, String group) {
		if (sender.getGroups().contains(group) || sender instanceof ConsoleCommandSender)
			return true;
		sendError(sender, "You must be in group '" + group + "' to do that!");
		return false;
	}

	public static boolean hasRank(CommandSender sender, byte level) {
		if (sender instanceof ProxiedPlayer) {
			if (ServerPlayer.getByPlayer((ProxiedPlayer) sender).getRank().hasRank(level))
				return true;
		} else if (sender instanceof ConsoleCommandSender)
			return true;
		sendError(sender, "You must have rank level '" + level + "' to do that!");
		return false;
	}

	public static boolean hasRank(CommandSender sender, Rank rank) {
		if (sender instanceof ProxiedPlayer) {
			if (ServerPlayer.getByPlayer((ProxiedPlayer) sender).getRank().hasRank(rank))
				return true;
		} else if (sender instanceof ConsoleCommandSender)
			return true;
		sendError(sender, "You must have rank '" + rank.name + "' to do that!");
		return false;
	}

	public static boolean isAdmin(CommandSender sender) {
		if (sender instanceof ProxiedPlayer) {
			if (ServerPlayer.getByPlayer((ProxiedPlayer) sender).getRank().isAdmin())
				return true;
		} else if (sender instanceof ConsoleCommandSender)
			return true;
		sendError(sender, "You must have admin permissions to do that!");
		return false;
	}

	public static boolean hasArgSize(CommandSender sender, String[] args, int size) {
		return hasArgSize(sender, args, new int[] { size });
	}

	public static boolean hasArgSize(CommandSender sender, String[] args, int[] sizes) {
		int argSize = args.length;
		for (int size : sizes) {
			if (size == argSize)
				return true;
		}
		sendError(sender, "Command must have " + Arrays.toString(sizes) + " arguments!");
		return false;
	}

	public static boolean argsSizeEquals(String[] args, int size) {
		if (args.length == size)
			return true;
		return false;
	}

	public static boolean argsSmallerThen(String[] args, int size) {
		if (args.length < size)
			return true;
		return false;
	}

	public static boolean argsBiggerThen(String[] args, int size) {
		if (args.length > size)
			return true;
		return false;
	}

	public static byte parseByte(CommandSender sender, String b) {
		try {
			return Byte.parseByte(b);
		} catch (NumberFormatException e) {
			sendError(sender, "Your number '" + b + "' is not formated properly!");
			return 0;
		}
	}

	public static short parseShort(CommandSender sender, String s) {
		try {
			return Short.parseShort(s);
		} catch (NumberFormatException e) {
			sendError(sender, "Your number '" + s + "' is not formated properly!");
			return 0;
		}
	}

	public static int parseInt(CommandSender sender, String i) {
		try {
			return Integer.parseInt(i);
		} catch (NumberFormatException e) {
			sendError(sender, "Your number '" + i + "' is not formated properly!");
			return 0;
		}
	}

	public static float parseFloat(CommandSender sender, String f) {
		try {
			return Float.parseFloat(f);
		} catch (NumberFormatException e) {
			sendError(sender, "Your number '" + f + "' is not formated properly!");
			return 0;
		}
	}

	public static double parseDouble(CommandSender sender, String d) {
		try {
			return Double.parseDouble(d);
		} catch (NumberFormatException e) {
			sendError(sender, "Your number '" + d + "' is not formated properly!");
			return 0;
		}
	}
}
