package com.emarinel.common;

/**
 * @author emarinelli
 */
public final class Some<T> implements Option<T> {

	private static final long serialVersionUID = 1L;

	private T value;

	private Some() {}

	private Some(T value) {
		this.value = value;
	}

	public static <T> Some<T> of(T value) {
		return new Some<T>(value);
	}

	public T get() {
		return this.value;
	}

	@Override
	public String toString() {
		return "Some(" + this.value + ")";
	}

	@Override
	public int hashCode() {
		return value == null ? 0 : value.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Some)) {
			return false;
		}

		Some o = (Some)other;
		return o.value.equals(this.value);
	}
}
