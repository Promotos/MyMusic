package de.promotos.mm.service.google;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import de.promotos.mm.MyMusicApp;
import de.promotos.mm.service.DriveApi;
import de.promotos.mm.service.ServiceException;

public class GDriveInstance implements DriveApi {

	private final static Logger LOG = Logger.getLogger(GDriveInstance.class.getName());
	
	/**
	 * Be sure to specify the name of your application. If the application name is
	 * {@code null} or blank, the application will log a warning. Suggested format
	 * is "MyCompany-ProductName/1.0".
	 */
	private static final String APPLICATION_NAME = "Promotos-MyMusic/1.0";

	/** Directory to store user credentials. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".myMusic");

	private static final String CLOUD_BASE_FOLDER = "myMusic";
	
	private final static String MIME_TYPE_FOLDER = "application/vnd.google-apps.folder";
	
	/**
	 * Global instance of the {@link DataStoreFactory}. The best practice is to make
	 * it a single globally shared instance across your application.
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
			drive = new Drive.Builder(httpTransport, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();

			LOG.log(Level.INFO, "Connected to Google Drive.");
			/*
			System.out.println(drive.getApplicationName());

			FileList result = drive.files().list().setQ("mimeType = 'audio/mp3'").execute();

			List<File> files = result.getFiles();
			if (files == null || files.isEmpty()) {
				System.out.println("No files found - create one.");
				//uploadFile();
			} else {
				System.out.println("Files:");
				for (File file : files) {
					System.out.printf("%s - %s (%s)\n", file.getName(), file.getMimeType(), file.getId());
				}
			}*/
			
		} catch (Exception e) {
			throw new ServiceException("Unable to connect to Google Drive.", e);
		}
	}
	
	@Override
	public boolean isConnected() {
		return drive != null;
	}

	/*
	private File uploadFile() throws IOException {
		java.io.File f = new java.io.File("d:\\alf.mp3");
		if (!f.exists()) {
			return null;
		}

		File fileMetadata = new File();
		fileMetadata.setName(f.getName());

		FileContent mediaContent = new FileContent("audio/mp3", f);

		Drive.Files.Create insert = drive.files().create(fileMetadata, mediaContent);
		MediaHttpUploader uploader = insert.getMediaHttpUploader();
		uploader.setDirectUploadEnabled(true);
		// uploader.setProgressListener(new FileUploadProgressListener());
		return insert.execute();
	}
	*/

	
	/** Downloads a file using either resumable or direct media download.
	private static void downloadFile(boolean useDirectDownload, File uploadedFile) throws IOException {
		// create parent directory (if necessary)
		java.io.File parentDir = new java.io.File("d:\\temp");
		if (!parentDir.exists() && !parentDir.mkdirs()) {
			throw new IOException("Unable to create parent directory");
		}
		OutputStream out = new FileOutputStream(new java.io.File(parentDir, uploadedFile.getName()));

		MediaHttpDownloader downloader = new MediaHttpDownloader(httpTransport,
				drive.getRequestFactory().getInitializer());
		downloader.setDirectDownloadEnabled(useDirectDownload);
		// downloader.setProgressListener(new FileDownloadProgressListener());
		// downloader.download(new GenericUrl(uploadedFile.ur getDownloadUrl()), out);
	}
	*/

	private Credential authorize() throws Exception {
		final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
				new InputStreamReader(MyMusicApp.class.getResourceAsStream("/secret.json")));
		final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
				clientSecrets, Collections.singleton(DriveScopes.DRIVE)).setDataStoreFactory(dataStoreFactory).build();
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	@Override
	public void initialize() throws ServiceException {
		final Optional<File> baseFolder = getFolder(CLOUD_BASE_FOLDER);
		
		if ( ! baseFolder.isPresent()) {
			LOG.log(Level.INFO, "Cloud base folder is not available.");
			createFolder(CLOUD_BASE_FOLDER);
			final Optional<File> verify = getFolder(CLOUD_BASE_FOLDER);
			if ( ! verify.isPresent()) {
				throw new ServiceException("Cloud base folder could not be created.");
			} else {
				LOG.log(Level.INFO, "Cloud base folder is created.");				
			}
		} else {
			LOG.log(Level.INFO, "Cloud base folder is available.");
		}
	}
	
	private File createFolder(final String name) throws ServiceException {
		try {
			final File fileMetadata = new File();
			fileMetadata.setName(CLOUD_BASE_FOLDER);
			fileMetadata.setMimeType(MIME_TYPE_FOLDER);

			final File file = drive.files().create(fileMetadata)
			    .setFields("id")
			    .execute();
			return file;
		} catch (IOException e) {
			throw new ServiceException("Could not create base folder.", e);
		}
	}
	
	private Optional<File> getFolder(final String name) throws ServiceException {
		try {
			final List<File> matches = drive.files().list()
				.setQ(String.format("name='%s' and mimeType = '%s'", CLOUD_BASE_FOLDER, MIME_TYPE_FOLDER)).execute()
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
