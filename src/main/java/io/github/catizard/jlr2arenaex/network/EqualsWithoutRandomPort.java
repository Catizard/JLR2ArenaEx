package io.github.catizard.jlr2arenaex.network;

/**
 * Similar to equals but ignore random port data. This interface is intended for internal testing.
 */
public interface EqualsWithoutRandomPort<T> {
	boolean equalsWithoutRandomPort(T obj);
}
