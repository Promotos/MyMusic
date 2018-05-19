package de.promotos.mm.service;

import java.io.IOException;
import java.util.logging.LogManager;

public final class Logging {

	public final static void enable() throws SecurityException, IOException {
			LogManager.getLogManager().readConfiguration(Logging.class.getResourceAsStream("/logging.properties"));
	}
	
}
