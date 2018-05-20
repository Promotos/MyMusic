package de.promotos.mm.service;

import java.io.IOException;
import java.util.logging.LogManager;

public final class Logging {

	private Logging() {}
	
	public static final void enable() throws IOException {
		LogManager.getLogManager().readConfiguration(Logging.class.getResourceAsStream("/logging.properties"));
	}
	
}
