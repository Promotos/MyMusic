package de.promotos.mm.service.google;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import de.promotos.mm.MyMusicApp;
import de.promotos.mm.service.Assert;
import de.promotos.mm.service.CloudApi;
import de.promotos.mm.service.ServiceException;
import de.promotos.mm.service.model.FileModel;
import de.promotos.mm.service.model.ModelFactory;

/**
 * The Google Drive Cloud api implementation.
 * 
 * @author Promotos
 *
 */
public class GDriveInstance implements CloudApi {

	private static final Logger LOG = Logger.getLogger(GDriveInstance.class.getName());

	/**
	 * Be sure to specify the name of your application. If the application name
	 * is {@code null} or blank, the application will log a warning. Suggested
	 * format is "MyCompany-ProductName/1.0".
	 */
	private static final String APPLICATION_NAME = "Promotos-MyMusic/1.0";

	/** Directory to store user credentials. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".myMusic");

	private static final @Nonnull String BASE_FOLDER = "myMusic";
	private File baseFolder;

	private static final @Nonnull String MIME_TYPE_FOLDER = "application/vnd.google-apps.folder";
	private static final @Nonnull String MIME_TYPE_MPEG = "audio/mpeg";

	/**
	 * Global instance of the {@link DataStoreFactory}. The best practice is to
	 * make it a single globally shared instance across your application.
	 */
	private FileDataStoreFactory dataStoreFactory;

	/** Global instance of the HTTP transport. */
	private HttpTransport httpTransport;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global Drive API client. */
	private Drive drive;

	@Override
	public void connect() throws ServiceException {
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

			// authorization
			final Credential credential = authorize();

			// set up the global Drive instance
			drive = new Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
					.build();

			LOG.log(Level.INFO, "Connected to Google Drive.");
		} catch (Exception e) {
			throw new ServiceException("Unable to connect to Google Drive.", e);
		}
	}

	@Override
	public boolean isConnected() {
		return drive != null;
	}

	/**
	 * Downloads a file using either resumable or direct media download. private
	 * static void downloadFile(boolean useDirectDownload, File uploadedFile)
	 * throws IOException { // create parent directory (if necessary)
	 * java.io.File parentDir = new java.io.File("d:\\temp"); if
	 * (!parentDir.exists() && !parentDir.mkdirs()) { throw new
	 * IOException("Unable to create parent directory"); } OutputStream out = new
	 * FileOutputStream(new java.io.File(parentDir, uploadedFile.getName()));
	 * 
	 * MediaHttpDownloader downloader = new MediaHttpDownloader(httpTransport,
	 * drive.getRequestFactory().getInitializer());
	 * downloader.setDirectDownloadEnabled(useDirectDownload); //
	 * downloader.setProgressListener(new FileDownloadProgressListener()); //
	 * downloader.download(new GenericUrl(uploadedFile.ur getDownloadUrl()),
	 * out); }
	 */

	private Credential authorize() throws IOException {
		final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
				new InputStreamReader(MyMusicApp.class.getResourceAsStream("/secret.json")));
		final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
				clientSecrets, Collections.singleton(DriveScopes.DRIVE)).setDataStoreFactory(dataStoreFactory).build();
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	@Override
	public void initialize() throws ServiceException {
		final Optional<File> baseFolderOpt = getFolder(BASE_FOLDER);

		if (!baseFolderOpt.isPresent()) {
			LOG.log(Level.INFO, "Cloud base folder is not available.");
			createFolder(BASE_FOLDER);
			final Optional<File> verify = getFolder(BASE_FOLDER);
			if (!verify.isPresent()) {
				throw new ServiceException("Cloud base folder could not be created.");
			} else {
				baseFolder = verify.get();
				LOG.log(Level.INFO, "Cloud base folder is created.");
			}
		} else {
			baseFolder = baseFolderOpt.get();
			LOG.log(Level.INFO, "Cloud base folder is available.");
		}
	}

	@Override
	public List<FileModel> listAudioFiles() throws ServiceException {
		try {
			final String q = "'" + baseFolder.getId() + "' in parents";
			final FileList result = drive.files().list().setQ(q).execute();

			return Collections.unmodifiableList(
					result.getFiles().stream()
							.filter(f -> f.getName().endsWith(".mp3"))
							.map(f -> ModelFactory.createFile(Assert.nN(f.getId()), Assert.nN(f.getName())))
							.collect(Collectors.toList()));
		} catch (IOException e) {
			throw new ServiceException("Error while list the audio files.", e);
		}
	}

	@Override
	public FileModel uploadFile(java.io.File file) throws ServiceException {
		try {
			final File fileMetadata = new File();
			fileMetadata.setName(file.getName());
			fileMetadata.setParents(Arrays.asList(baseFolder.getId()));

			final FileContent mediaContent = new FileContent(MIME_TYPE_MPEG, file);

			final File result = drive.files().create(fileMetadata, mediaContent)
					.setFields("id,name")
					.execute();

			LOG.log(Level.INFO, "Upload file {0}", file.getName());
			return ModelFactory.createFile(Assert.nN(result.getId()), Assert.nN(result.getName()));
		} catch (IOException e) {
			throw new ServiceException("Could not upload file " + file.getName(), e);
		}
	}

	@Override
	public void deleteFile(final FileModel file) throws ServiceException {
		try {
			if (file.getId() != null) {
				LOG.log(Level.INFO, "Delete file {0}", file.getName());
				drive.files().delete(String.valueOf(file.getId())).execute();
			}
		} catch (IOException e) {
			throw new ServiceException("Could not delete file " + file.getName(), e);
		}
	}

	@Override
	public InputStream readFile(final FileModel file) throws ServiceException {
		try {
			return drive.files().get((String.valueOf(file.getId()))).executeMediaAsInputStream();
		} catch (IOException e) {
			throw new ServiceException("Could not create stream to file " + file.getName(), e);
		}

	}

	private File createFolder(final String name) throws ServiceException {
		try {
			final File fileMetadata = new File();
			fileMetadata.setName(name);
			fileMetadata.setMimeType(MIME_TYPE_FOLDER);

			return drive.files().create(fileMetadata).setFields("id").execute();
		} catch (IOException e) {
			throw new ServiceException("Could not create base folder.", e);
		}
	}

	private Optional<File> getFolder(final String name) throws ServiceException {
		try {
			final List<File> matches = drive.files().list()
					.setQ(String.format("name='%s' and mimeType = '%s'", name, MIME_TYPE_FOLDER))
					.execute()
					.getFiles();

			if (matches.isEmpty()) {
				return Optional.empty();
			} else if (matches.size() > 1) {
				throw new ServiceException("More than one folder matches the name");
			}

			return Optional.of(matches.get(0));
		} catch (IOException e) {
			throw new ServiceException("Error while reading folder structure");
		}
	}
}
