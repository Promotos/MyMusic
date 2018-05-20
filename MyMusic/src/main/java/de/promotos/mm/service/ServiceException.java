package de.promotos.mm.service;

/**
 * Generic exception for all service calls.
 * 
 * @author Promotos
 *
 */
@SuppressWarnings("serial")
public class ServiceException extends Exception {

	/**
	 * Create a new exception instance.
	 */
	public ServiceException() {
		super();
	}

	/**
	 * Create a new exception instance.
	 * 
	 * @param message
	 *           The exception message.
	 */
	public ServiceException(final String message) {
		super(message);
	}

	/**
	 * Create a new exception instance.
	 * 
	 * @param message
	 *           The exception message.
	 * @param cause
	 *           The cause of this exception
	 */
	public ServiceException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
