package de.promotos.mm.service;

import java.io.File;
import java.util.Optional;

import javax.annotation.Nonnull;

import de.promotos.mm.service.google.GDriveInitTask;
import de.promotos.mm.service.google.GDriveUploadTask;
import de.promotos.mm.service.model.FileModel;
import javafx.concurrent.Task;

/**
 * Factory to create the init task. Used to select the cloud backend.
 * 
 * @author Promotos
 *
 */
public class TaskFactory {

	/**
	 * Init task to the selected cloud backend.
	 * 
	 * @return The task instance.
	 */
	public @Nonnull Task<CloudApi> buildInitTask() {
		return new GDriveInitTask();
	}

	/**
	 * Task to upload one file.
	 * 
	 * @param api
	 *           The cloud api instance.
	 * @param file
	 *           The file to upload.
	 * @return The FileModel instance as optional or null if the upload was not
	 *         executed. Maybe the file does not exist, is not readable or is not
	 *         an mp3-file.
	 */
	public @Nonnull Task<Optional<FileModel>> buildUploadTask(final CloudApi api, final File file) {
		return new GDriveUploadTask(api, file);
	}
}
