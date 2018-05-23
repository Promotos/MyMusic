package de.promotos.mm.service;

/**
 * Executed as callback if an error occur.
 * 
 * @author Promotos
 *
 */
public interface ErrorRunnable {

	/**
	 * Called on an error.
	 * 
	 * @param error
	 *           The cause of the error.
	 */
	void onError(Throwable error);

}
