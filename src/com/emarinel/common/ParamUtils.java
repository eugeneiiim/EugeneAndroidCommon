package com.emarinel.common;

public final class ParamUtils {
	public static void checkNotNull(Object arg, String name) {
		if (arg == null) {
			throw new IllegalArgumentException(name + " must not be null.");
		}
	}

	public static void checkNotBlank(String string, String varname) {
		if (string == null) {
			throw new IllegalArgumentException(varname + " must not be null.");
		}

		if (string.trim().length() == 0) {
			throw new IllegalArgumentException(varname + " must not be blank.");
		}
	}
}
