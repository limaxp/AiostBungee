package com.pm.aiost.misc.command.commands;

import static com.pm.aiost.misc.command.CommandHelper.*;
import static com.pm.aiost.misc.command.CommandHelper.isAdmin;
import static com.pm.aiost.misc.command.CommandHelper.isPlayerOrConsole;
import static com.pm.aiost.misc.command.CommandHelper.sendError;

import java.io.File;
import java.net.InetSocketAddress;

import com.pm.aiost.server.Server;
import com.pm.aiost.server.ServerBuilder;
import com.pm.aiost.server.ServerLoader;
import com.pm.aiost.server.ServerType;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ServerCommands {

	public static class ReloadCommand extends Command {

		public ReloadCommand() {
			super("reload", null, new String[0]);
		}

		@Override
		public void execute(CommandSender sender, String[] args) {
			if (hasArgSize(sender, args, 0)) {
				if (isPlayerOrConsole(sender)) {
					if (isAdmin(sender))
						BungeeCord.getInstance().config.load();
				}
			}
		}
	}

	public static class StopCommand extends Command {

		public StopCommand() {
			super("stop", null, new String[0]);
		}

		@Override
		public void execute(CommandSender sender, String[] args) {
			if (hasArgSize(sender, args, 0)) {
				if (isPlayerOrConsole(sender)) {
					if (isAdmin(sender))
						BungeeCord.getInstance().stop();
				}
			}
		}
	}

	public static class RegisterServerCommand extends Command {

		public RegisterServerCommand() {
			super("registerServer", null, new String[0]);
		}

		@Override
		public void execute(CommandSender sender, String[] args) {
			if (hasArgSize(sender, args, 5)) {
				if (isPlayerOrConsole(sender)) {
					if (isAdmin(sender)) {
						Server server = registerServer(sender, args);
						sendMsg(sender, "Server '" + server.info.getName() + "' successfully registered!");
					}
				}
			}
		}

		private static Server registerServer(CommandSender sender, String[] args) {
			String name = args[0].replace("_", " ");
			ServerType serverType = ServerType.getIgnoreCase(args[1]);
			if (serverType == null) {
				sendError(sender, "No server type found for name '" + args[1] + "'");
				return null;
			}
			String motd = args[2].replace("_", " ");
			String address = args[3];
			int port = Integer.parseInt(args[4]);
			InetSocketAddress connection;
			try {
				connection = new InetSocketAddress(address, port);
			} catch (Exception e) {
				sendError(sender, "Your address is not formated correctly!");
				return null;
			}
			return ServerLoader.registerServer(name, serverType, motd, false, connection);
		}
	}

	public static class UnregisterServerCommand extends Command {

		public UnregisterServerCommand() {
			super("unregisterServer", null, new String[0]);
		}

		@Override
		public void execute(CommandSender sender, String[] args) {
			if (hasArgSize(sender, args, 1)) {
				if (isPlayerOrConsole(sender)) {
					if (isAdmin(sender)) {
						String name = args[0];
						Server server = Server.getByName(name);
						if (server == null) {
							sendError(sender, "No server found for name '" + name + "'");
							return;
						}
						ServerLoader.unregisterServer(server);
						sendMsg(sender, "Server '" + server.info.getName() + "' successfully unregistered!");
					}
				}
			}
		}
	}

	public static class CreateServerCommand extends Command {

		public CreateServerCommand() {
			super("createServer", null, new String[0]);
		}

		@Override
		public void execute(CommandSender sender, String[] args) {
			if (hasArgSize(sender, args, 2)) {
				if (isPlayerOrConsole(sender)) {
					if (isAdmin(sender)) {
						String name = args[0];
						Server server = Server.getByName(name);
						if (server == null) {
							sendError(sender, "No server found for name '" + name + "'");
							return;
						}
						String filePath = args[1];
						if (filePath.isEmpty()) {
							sendError(sender, "File name was empty");
							return;
						}
						ServerBuilder.createServer(server, new File(filePath));
						sendMsg(sender, "Server '" + server.info.getName() + "' successfully created!");
					}
				}
			}
		}
	}

	public static class StartServerCommand extends Command {

		public StartServerCommand() {
			super("startServer", null, new String[0]);
		}

		@Override
		public void execute(CommandSender sender, String[] args) {
			if (hasArgSize(sender, args, 2)) {
				if (isPlayerOrConsole(sender)) {
					if (isAdmin(sender)) {
						String name = args[0];
						Server server = Server.getByName(name);
						if (server == null) {
							sendError(sender, "No server found for name '" + name + "'");
							return;
						}
						String filePath = args[1];
						if (filePath.isEmpty()) {
							sendError(sender, "File name was empty");
							return;
						}
						ServerBuilder.startServer(server, new File(filePath));
						sendMsg(sender, "Server '" + server.info.getName() + "' successfully started!");
					}
				}
			}
		}
	}

	public static class DeleteServerCommand extends Command {

		public DeleteServerCommand() {
			super("deleteServer", null, new String[0]);
		}

		@Override
		public void execute(CommandSender sender, String[] args) {
			if (hasArgSize(sender, args, 1)) {
				if (isPlayerOrConsole(sender)) {
					if (isAdmin(sender)) {
						String name = args[0];
						Server server = Server.getByName(name);
						if (server == null) {
							sendError(sender, "No server found for name '" + name + "'");
							return;
						}
						ServerBuilder.deleteServer(server);
						sendMsg(sender, "Server '" + server.info.getName() + "' successfully deleted!");
					}
				}
			}
		}
	}

	public static class StopServerCommand extends Command {

		public StopServerCommand() {
			super("stopServer", null, new String[0]);
		}

		@Override
		public void execute(CommandSender sender, String[] args) {
			if (hasArgSize(sender, args, 1)) {
				if (isPlayerOrConsole(sender)) {
					if (isAdmin(sender)) {
						String name = args[0];
						Server server = Server.getByName(name);
						if (server == null) {
							sendError(sender, "No server found for name '" + name + "'");
							return;
						}
						ServerBuilder.stopServer(server);
						sendMsg(sender, "Server '" + server.info.getName() + "' successfully stoped!");
					}
				}
			}
		}
	}
}
