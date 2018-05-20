package de.promotos.mm.service;

import java.io.IOException;
import java.util.logging.LogManager;

/**
 * Used to configure the logging behavior.
 * 
 * @author Promotos
 *
 */
public final class Logging {

	private Logging() {
	}

	/**
	 * Enable and configure the jdk logging. Logging configuration is set in the
	 * file "/logging.properties"
	 * 
	 * @throws IOException
	 */
	public static final void enable() throws IOException {
		LogManager.getLogManager().readConfiguration(Logging.class.getResourceAsStream("/logging.properties"));
	}

}
