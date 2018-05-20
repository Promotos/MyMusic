package de.promotos.mm.service.model;

/**
 * Represent one audio file.
 * 
 * @author Promotos
 *
 */
public interface FileModel {

	/**
	 * The id to identify this file.
	 * 
	 * @return The id to be casted by the cloud api.
	 */
	Object getId();

	/**
	 * Return the name of the file including extension.
	 * 
	 * @return The file name with extension.
	 */
	String getName();
}
