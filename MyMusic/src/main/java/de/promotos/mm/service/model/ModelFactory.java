package de.promotos.mm.service.model;

/**
 * Factory to create model instances.
 * 
 * @author Promotos
 *
 */
public final class ModelFactory {

	private ModelFactory() {
	}

	/**
	 * Create a audio file instance.
	 * 
	 * @param id
	 *           The id to identify the file.
	 * @param name
	 *           The file name.
	 * @return The file model instance.
	 */
	public static final FileModel createFile(final Object id, final String name) {
		return new FileModelImpl(id, name);
	}

}
