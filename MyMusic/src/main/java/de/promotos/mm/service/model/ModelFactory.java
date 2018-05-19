package de.promotos.mm.service.model;

public final class ModelFactory {

	private ModelFactory() {}
	
	public static final FileModel createFile(final Object id, final String name) {
		return new FileModelImpl(id, name);
	}
	
}
