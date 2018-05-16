package de.promotos.mm.service;

public interface DriveApi {

	void connect() throws ServiceException;
	boolean isConnected();
}
