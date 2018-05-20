package de.promotos.mm.service;

import javax.annotation.Nonnull;

import de.promotos.mm.service.google.GDriveInitTask;
import javafx.concurrent.Task;

/**
 * Factory to create the init task. Used to select the cloud backend.
 * 
 * @author Promotos
 *
 */
public class InitTaskFactory {

	/**
	 * Init task to the selected cloud backend.
	 * 
	 * @return The task instance.
	 */
	public @Nonnull Task<CloudApi> get() {
		return new GDriveInitTask();
	}

}
