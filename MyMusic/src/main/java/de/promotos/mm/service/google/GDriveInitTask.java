package de.promotos.mm.service.google;

import de.promotos.mm.service.CloudApi;
import de.promotos.mm.service.ServiceException;
import javafx.concurrent.Task;

/**
 * Task to initialize the Google Drive Cloud backend.
 * 
 * @author Promotos
 *
 */
public class GDriveInitTask extends Task<CloudApi> {

	@Override
	protected CloudApi call() throws Exception {
		final CloudApi api = new GDriveInstance();

		updateMessage("Connect to GDrive");
		api.connect();
		if (!api.isConnected()) {
			throw new ServiceException("Unable to connect to cloud");
		}

		updateMessage("Initialize GDrive");
		api.initialize();

		return api;
	}

}
