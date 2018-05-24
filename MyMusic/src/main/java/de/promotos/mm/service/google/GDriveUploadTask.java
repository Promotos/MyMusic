package de.promotos.mm.service.google;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import de.promotos.mm.service.CloudApi;
import de.promotos.mm.service.FunctionWithException;
import de.promotos.mm.service.ServiceException;
import de.promotos.mm.service.model.FileModel;
import javafx.concurrent.Task;

/**
 * Task to upload one file.
 * 
 * @author Promotos
 *
 */
public class GDriveUploadTask extends Task<List<FileModel>> {

	private final @Nonnull CloudApi api;
	private final @Nonnull List<File> files;
	private final long max;
	private int count = 0;

	/**
	 * Create a new instance of the upload task
	 * 
	 * @param api
	 *           The api instance.
	 * @param files
	 *           The files to upload.
	 */
	public GDriveUploadTask(final CloudApi api, final List<java.io.File> files) {
		this.api = api;
		this.files = files;
		max = files.size();
	}

	@Override
	protected List<FileModel> call() throws Exception {
		return files.stream()
				.filter(file -> !file.exists() || !file.canRead() || !file.getName().endsWith("*.mp3"))
				.map(FunctionWithException.wrapper(file -> uploadFile(file)))
				.collect(Collectors.toList());
	}

	private FileModel uploadFile(File f) throws ServiceException {
		updateMessage("Upload " + f.getName());
		updateProgress(count++, max);
		return api.uploadFile(f);
	}
}
