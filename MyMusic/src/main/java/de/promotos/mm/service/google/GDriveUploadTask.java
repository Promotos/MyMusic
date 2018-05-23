package de.promotos.mm.service.google;

import java.util.Optional;

import javax.annotation.Nonnull;

import de.promotos.mm.service.CloudApi;
import de.promotos.mm.service.model.FileModel;
import javafx.concurrent.Task;

/**
 * Task to upload one file.
 * 
 * @author Promotos
 *
 */
public class GDriveUploadTask extends Task<Optional<FileModel>> {

	private final @Nonnull CloudApi api;
	private final @Nonnull java.io.File file;

	/**
	 * Create a new instance of the upload task
	 * 
	 * @param api
	 *           The api instance.
	 * @param file
	 *           The file to upload.
	 */
	public GDriveUploadTask(final CloudApi api, final java.io.File file) {
		this.api = api;
		this.file = file;
	}

	@Override
	protected Optional<FileModel> call() throws Exception {
		updateMessage("Upload " + file.getName());
		if (file.exists() && file.canRead() && file.getName().endsWith("*.mp3")) {
			return Optional.of(api.uploadFile(file));
		}
		return Optional.ofNullable(null);
	}

}
