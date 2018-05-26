package de.promotos.mm.service;

import java.io.InputStream;
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
	 * Get the user quota string.
	 * 
	 * @return The string with user quota information.
	 * @throws ServiceException
	 *            Thrown if the api call failed.
	 */
	String getUserQuota() throws ServiceException;

	/**
	 * List all remote available audio files.
	 * 
	 * @return The list of available audio files.
	 * @throws ServiceException
	 *            Thrown if the api call failed.
	 */
	List<FileModel> listAudioFiles() throws ServiceException;

	/**
	 * Upload one file to the cloud backend folder.
	 * 
	 * @param file
	 *           The file to upload.
	 * @return The file model.
	 * @throws ServiceException
	 *            Thrown if the api call failed.
	 */
	FileModel uploadFile(java.io.File file) throws ServiceException;

	/**
	 * Delete a cloud files.
	 * 
	 * @param file
	 *           The file model to delete.
	 * @throws ServiceException
	 *            If the api call throw an error.
	 */
	void deleteFile(FileModel file) throws ServiceException;

	/**
	 * Get the input stream for the content of a give file. The caller must close
	 * this stream.
	 * 
	 * @param file
	 *           The file instance to read the input for.
	 * @return The input stream to read the content from.
	 * @throws ServiceException
	 *            If the api call throw an error.
	 */
	public InputStream readFile(final FileModel file) throws ServiceException;
}
