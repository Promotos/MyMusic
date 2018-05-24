package de.promotos.mm.service;

import java.util.function.Function;

/**
 * Function wrapper with exceptions
 * 
 * @author Promotos
 *
 * @param <T>
 * @param <R>
 * @param <E>
 */
public interface FunctionWithException<T, R, E extends Exception> {

	/**
	 * Execute the function.
	 * 
	 * @param t
	 *           The argument
	 * @return The result
	 * @throws E
	 *            The thrown exception.
	 */
	R apply(T t) throws E;

	/**
	 * Wrapper for a lambda
	 * 
	 * @param fe
	 *           The function.
	 * @return The wrapped function.
	 */
	public static <T, R, E extends Exception> Function<T, R> wrapper(FunctionWithException<T, R, E> fe) {
		return arg -> {
			try {
				return fe.apply(Assert.nN(arg));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}
}
