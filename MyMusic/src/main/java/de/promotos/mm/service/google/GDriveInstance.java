package de.promotos.mm.service.google;

import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

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
import com.google.api.services.drive.model.FileList;

import de.promotos.mm.MyMusicApp;
import de.promotos.mm.service.DriveApi;
import de.promotos.mm.service.ServiceException;

public class GDriveInstance implements DriveApi {

	/**
	 * Be sure to specify the name of your application. If the application name is
	 * {@code null} or blank, the application will log a warning. Suggested format
	 * is "MyCompany-ProductName/1.0".
	 */
	private static final String APPLICATION_NAME = "Promotos-MyMusic/1.0";

	/** Directory to store user credentials. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".myMusic");

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

			//System.out.println(" connected as " + drive.about().get().execute().getUser().getDisplayName());
			
			System.out.println(drive.getApplicationName());

			FileList result = drive.files().list().setQ("mimeType = 'audio/mp3'").execute();
			// .setPageSize(10)
			// .setFields("nextPageToken, files(id, name)")

			List<File> files = result.getFiles();
			if (files == null || files.isEmpty()) {
				System.out.println("No files found - create one.");
				//uploadFile();
			} else {
				System.out.println("Files:");
				for (File file : files) {
					System.out.printf("%s - %s (%s)\n", file.getName(), file.getMimeType(), file.getId());
				}
			}
			
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
	
}
