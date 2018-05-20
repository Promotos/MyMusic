package de.promotos.mm.service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Assertions used to convert between {@link Nullable} and {@link Nonnull}.
 * 
 * @author Promotos
 *
 */
public final class Assert {

	private Assert() {
	}

	/**
	 * Non null check. Throws an {@link NullPointerException} if the argument is
	 * null.
	 * 
	 * @param inst
	 * @return The {@link Nonnull} checked intance of the argument.
	 */
	public final static @Nonnull <T> T nN(final @Nullable T inst) {
		if (inst == null) {
			throw new NullPointerException();
		}
		return inst;
	}

}
