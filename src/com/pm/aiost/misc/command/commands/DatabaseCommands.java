package com.pm.aiost.misc.command.commands;

import static com.pm.aiost.misc.command.CommandHelper.argsBiggerThen;
import static com.pm.aiost.misc.command.CommandHelper.hasArgSize;
import static com.pm.aiost.misc.command.CommandHelper.isAdmin;
import static com.pm.aiost.misc.command.CommandHelper.isPlayerOrConsole;
import static com.pm.aiost.misc.command.CommandHelper.sendError;
import static com.pm.aiost.misc.command.CommandHelper.sendMsg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pm.aiost.misc.database.Database;
import com.pm.aiost.misc.database.DatabaseBuilder;
import com.pm.aiost.misc.database.DatabaseManager;
import com.pm.aiost.misc.database.ScriptReader;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class DatabaseCommands {

	public static class BuildDatabaseCommand extends Command {

		public BuildDatabaseCommand() {
			super("buildDatabase", null, new String[0]);
		}

		@Override
		public void execute(CommandSender sender, String[] args) {
			if (hasArgSize(sender, args, 0)) {
				if (isPlayerOrConsole(sender)) {
					if (isAdmin(sender))
						DatabaseBuilder.buildDatabase();
				}
			}
		}
	}

	public static class QueryCommand extends Command {

		public QueryCommand() {
			super("query", null, new String[0]);
		}

		@Override
		public void execute(CommandSender sender, String[] args) {
			if (argsBiggerThen(args, 0)) {
				if (isPlayerOrConsole(sender)) {
					if (isAdmin(sender)) {
						ArrayList<Object[]> result = DatabaseManager.getDatabase().query(String.join(" ", args));
						sendMsg(sender, "Database query complete with result:\n" + Database.resultListToString(result));
					}
				}
			} else
				sendError(sender, "Command must have at least 1 argument!");
		}
	}

	public static class UpdateQueryCommand extends Command {

		public UpdateQueryCommand() {
			super("updateQuery", null, new String[0]);
		}

		@Override
		public void execute(CommandSender sender, String[] args) {
			if (argsBiggerThen(args, 0)) {
				if (isPlayerOrConsole(sender)) {
					if (isAdmin(sender)) {
						int result = DatabaseManager.getDatabase().update(String.join(" ", args));
						sendMsg(sender, "Database query complete with result: " + result);
					}
				}
			} else
				sendError(sender, "Command must have at least 1 argument!");
		}
	}

	public static class ExecuteQueryCommand extends Command {

		public ExecuteQueryCommand() {
			super("execQuery", null, new String[0]);
		}

		@Override
		public void execute(CommandSender sender, String[] args) {
			if (argsBiggerThen(args, 0)) {
				if (isPlayerOrConsole(sender)) {
					if (isAdmin(sender)) {
						boolean result = DatabaseManager.getDatabase().exec(String.join(" ", args));
						sendMsg(sender, "Database query complete with result: " + result);
					}
				}
			} else
				sendError(sender, "Command must have at least 1 argument!");
		}
	}

	public static class CallQueryCommand extends Command {

		public CallQueryCommand() {
			super("callQuery", null, new String[0]);
		}

		@Override
		public void execute(CommandSender sender, String[] args) {
			if (argsBiggerThen(args, 0)) {
				if (isPlayerOrConsole(sender)) {
					if (isAdmin(sender)) {
						ArrayList<Object[]> result;
						if (argsBiggerThen(args, 1)) {
							List<Object> argObjects = new ArrayList<Object>();
							String arg;
							for (int i = 1; i < args.length; i++) { // TODO: make real arg parsing here and in other
																	// command!
								arg = args[i];
								if (arg.matches(".*\\d+.*"))
									argObjects.add(Integer.parseInt(arg));
								else
									argObjects.add(arg);
							}
							result = DatabaseManager.getDatabase().call("CALL " + String.join(" ", args), argObjects);
						} else
							result = DatabaseManager.getDatabase().call("CALL " + String.join(" ", args));

						sendMsg(sender, "Database call complete with result:\n" + Database.resultListToString(result));
					}
				}
			} else
				sendError(sender, "Command must have at least 1 argument!");
		}
	}

	public static class BatchQueryCommand extends Command {

		public BatchQueryCommand() {
			super("batchQuery", null, new String[0]);
		}

		@Override
		public void execute(CommandSender sender, String[] args) {
			if (argsBiggerThen(args, 0)) {
				if (isPlayerOrConsole(sender)) {
					if (isAdmin(sender)) {
						int[] result = DatabaseManager.getDatabase()
								.batch(ScriptReader.scriptToBatchList(String.join(" ", args)));
						sendMsg(sender, "Database query complete with result:\n" + Arrays.toString(result));
					}
				}
			} else
				sendError(sender, "Command must have at least 1 argument!");
		}
	}
}
