package de.promotos.mm.service;

import de.promotos.mm.service.google.GDriveInitTask;
import javafx.concurrent.Task;

public class InitTaskFactory {

	public Task<DriveApi> get() {
		return new GDriveInitTask();
	}
	
}
