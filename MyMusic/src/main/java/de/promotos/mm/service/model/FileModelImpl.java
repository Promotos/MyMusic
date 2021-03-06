package de.promotos.mm.service.model;

/**
 * Model class for files.
 * 
 * @author Promotos
 *
 */
public final class FileModelImpl implements FileModel {

	private final Object id;
	private final String name;

	FileModelImpl(Object id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public Object getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return String.format("id:%s, name:%s", id, name);
	}
}
