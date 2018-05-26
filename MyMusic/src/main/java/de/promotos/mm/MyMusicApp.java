package de.promotos.mm;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import de.promotos.mm.scene.ImageFactory;
import de.promotos.mm.scene.MainSceneController;
import de.promotos.mm.scene.ProgressScene;
import de.promotos.mm.scene.SceneFactory;
import de.promotos.mm.service.Assert;
import de.promotos.mm.service.CloudApi;
import de.promotos.mm.service.Logging;
import de.promotos.mm.service.TaskFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Main entry class to start the application. First shows the splash screen,
 * after successful connect to the cloud, forward to the main screen.
 * 
 * @author Promotos
 *
 */
public class MyMusicApp extends Application {

	/**
	 * Logger instance.
	 */
	private static final Logger LOG = Logger.getLogger(MyMusicApp.class.getName());

	/**
	 * Text shown as application title.
	 */
	private static final String APP_TITLE = "MyMusic - Cloud Music Player";

	/**
	 * Java application entry.
	 * 
	 * @param args
	 *           The command line arguments.
	 */
	public static void main(String[] args) {

		try {
			Logging.enable();
		} catch (SecurityException | IOException e) {
			throw new IllegalStateException("Unable to initialize logging.", e);
		}

		Application.launch(args);
	}

	/**
	 * @param primaryStage
	 */
	@Override
	public void start(final @Nullable Stage primaryStage) throws Exception {
		final Task<CloudApi> initTask = new TaskFactory().buildInitTask();
		new ProgressScene().show(Assert.nN(primaryStage), initTask, () -> showMainScene(initTask), MyMusicApp::onError);
		new Thread(initTask).start();
	}

	private static void onError(final Throwable e) {
		final Alert alert = new Alert(AlertType.ERROR, e.getMessage() +
				"\nApplication will exit.");
		alert.showAndWait();
		Platform.exit();
	}

	private static void showMainScene(Task<CloudApi> initTask) {
		try {
			final Stage mainStage = new Stage(StageStyle.DECORATED);
			mainStage.setTitle(APP_TITLE);
			mainStage.getIcons().add(ImageFactory.getAppIcon());

			final FXMLLoader loader = SceneFactory.loadMainScene();
			mainStage.setScene(new Scene(loader.load()));
			mainStage.show();

			final MainSceneController controller = loader.getController();
			mainStage.setOnCloseRequest((WindowEvent event1) -> {
				controller.shutdown();
				Platform.exit();
				System.exit(0);
			});
			controller.setStage(mainStage);
			controller.setApi(Assert.nN(initTask.valueProperty().get()));
			controller.refresh();

		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Error while show the main screen.", e);
		}
	}

}
