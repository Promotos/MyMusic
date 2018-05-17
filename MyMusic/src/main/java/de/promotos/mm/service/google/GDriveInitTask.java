package de.promotos.mm.service.google;

import de.promotos.mm.service.DriveApi;
import de.promotos.mm.service.ServiceException;
import javafx.concurrent.Task;

public class GDriveInitTask extends Task<DriveApi> {

	@Override
	protected DriveApi call() throws Exception {
		final DriveApi api = new GDriveInstance();

		updateMessage("Connect to GDrive");
		api.connect();
		if ( ! api.isConnected()) {
			throw new ServiceException("Unable to connect to cloud");
		}
		
		updateMessage("Initialize GDrive");
		api.initialize();
		
		return api;
	}

}
