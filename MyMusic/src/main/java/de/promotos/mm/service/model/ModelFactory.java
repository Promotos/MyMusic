package de.promotos.mm.service.model;

public final class ModelFactory {

	public final static FileModel createFile(final Object id, final String name) {
		return new FileModelImpl(id, name);
	}
	
}
