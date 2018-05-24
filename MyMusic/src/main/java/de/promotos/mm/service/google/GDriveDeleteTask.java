package de.promotos.mm.service.google;

import java.util.List;

import javax.annotation.Nonnull;

import de.promotos.mm.service.Assert;
import de.promotos.mm.service.CloudApi;
import de.promotos.mm.service.model.FileModel;
import javafx.concurrent.Task;

/**
 * Task to delete cloud files.
 * 
 * @author Promotos
 *
 */
public class GDriveDeleteTask extends Task<Boolean> {

	private final @Nonnull CloudApi api;
	final List<FileModel> files;

	/**
	 * Create a new instance of the upload task
	 * 
	 * @param api
	 *           The api instance.
	 * @param files
	 *           The files to delete.
	 */
	public GDriveDeleteTask(final CloudApi api, final List<FileModel> files) {
		this.api = api;
		this.files = files;
	}

	@Override
	protected Boolean call() throws Exception {
		long count = 0;
		for (final FileModel file : files) {
			updateProgress(++count, files.size());
			updateMessage("Delete " + file.getName());
			api.deleteFile(Assert.nN(file));
		}
		return Boolean.TRUE;
	}

}
