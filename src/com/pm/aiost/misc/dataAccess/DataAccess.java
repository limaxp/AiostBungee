package com.pm.aiost.misc.dataAccess;

public class DataAccess {

	private static BungeeDataAccess access;

	public static <T extends BungeeDataAccess> void init(T dataAccess) {
		if (DataAccess.access == null)
			DataAccess.access = dataAccess;
	}

	public static BungeeDataAccess getAccess() {
		return access;
	}
}
