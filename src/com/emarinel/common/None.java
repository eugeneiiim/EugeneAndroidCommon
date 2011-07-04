package com.emarinel.common;

/**
 * @author emarinelli
 */
public final class None<T> implements Option<T> {

	private static final long serialVersionUID = 1L;

	private static final None INSTANCE = new None();

	private None() {}

	public static <T> None<T> get() {
		return INSTANCE;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof None;
	}
}
