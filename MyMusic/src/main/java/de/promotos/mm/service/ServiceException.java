package de.promotos.mm.service;

@SuppressWarnings("serial")
public class ServiceException extends Exception {

	public ServiceException() { super(); }
	public ServiceException(final String message) { super(message); }
	public ServiceException(final String message, Throwable cause) { super(message, cause);}
	
}
