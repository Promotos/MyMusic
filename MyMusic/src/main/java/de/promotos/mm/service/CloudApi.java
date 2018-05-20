package de.promotos.mm.service;

import java.util.List;

import de.promotos.mm.service.model.FileModel;

/**
 * The Cloud Api.
 * 
 * @author Promotos
 *
 */
public interface CloudApi {

	/**
	 * Connect to the cloud service. Maybe requires a authentication - for google
	 * result in a url, opended in the default browser.
	 * 
	 * @throws ServiceException
	 *            Multiple reasons
	 */
	void connect() throws ServiceException;

	/**
	 * Check if the connection was successful.
	 * 
	 * @return <code>true</code> or <code>false</code>
	 */
	boolean isConnected();

	/**
	 * Initialize the cloud drive.
	 * <li>check for read permissions.
	 * <li>check for default folder existence.
	 * <li>create - if required - the default folder
	 * 
	 * @throws ServiceException
	 *            Multiple reasons
	 */
	void initialize() throws ServiceException;

	/**
	 * List all remote available audio files.
	 * 
	 * @return The list of available audio files.
	 * @throws ServiceException
	 *            Thrown if the api call failed.
	 */
	List<FileModel> listAudioFiles() throws ServiceException;
}
