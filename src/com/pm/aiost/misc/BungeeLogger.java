package com.pm.aiost.misc;

import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.pm.aiost.AiostBungee;
import com.pm.aiost.misc.log.AbstractLogger;

public class BungeeLogger extends AbstractLogger {

	public BungeeLogger() {
	}

	private static final Logger logger = AiostBungee.getPlugin().getLogger();

	@Override
	public void log(String msg) {
		logger.info(msg);
	}

	@Override
	public void log(Supplier<String> msg) {
		logger.info(msg);
	}

	@Override
	public void warn(String msg) {
		logger.warning(msg);
	}

	@Override
	public void warn(Supplier<String> msg) {
		logger.warning(msg);
	}

	@Override
	public void err(String msg, Throwable throwable) {
		logger.log(Level.WARNING, msg, throwable);
	}

	@Override
	public void err(Supplier<String> msg, Throwable throwable) {
		logger.log(Level.WARNING, throwable, msg);
	}

	@Override
	public void err(Level level, String msg, Throwable throwable) {
		logger.log(level, msg, throwable);
	}

	@Override
	public void err(Level level, Supplier<String> msg, Throwable throwable) {
		logger.log(level, throwable, msg);
	}
}
