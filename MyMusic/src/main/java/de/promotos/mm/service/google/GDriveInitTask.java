package de.promotos.mm.service.google;

import de.promotos.mm.service.DriveApi;
import javafx.concurrent.Task;

public class GDriveInitTask extends Task<DriveApi> {

	@Override
	protected DriveApi call() throws Exception {
		final DriveApi result = new GDriveInstance();

		updateMessage("Connect to GDrive");
		result.connect();
		
		return result;
	}

}
