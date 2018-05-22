package de.promotos.mm;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

import de.promotos.mm.scene.ImageFactory;
import de.promotos.mm.scene.MainSceneController;
import de.promotos.mm.scene.SceneFactory;
import de.promotos.mm.scene.SplashScene;
import de.promotos.mm.service.Assert;
import de.promotos.mm.service.CloudApi;
import de.promotos.mm.service.InitTaskFactory;
import de.promotos.mm.service.Logging;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
		final Task<CloudApi> initTask = new InitTaskFactory().get();
		new SplashScene().show(Assert.nN(primaryStage), initTask, () -> showMainScene(initTask));
		new Thread(initTask).start();
	}

	private static void showMainScene(Task<CloudApi> initTask) {
		try {
			final Stage mainStage = new Stage(StageStyle.DECORATED);
			mainStage.setTitle(APP_TITLE);
			mainStage.getIcons().add(ImageFactory.getAppIcon());

			final FXMLLoader loader = SceneFactory.loadMainScene();
			mainStage.setScene(new Scene(loader.load()));
			mainStage.show();

			MainSceneController controller = loader.getController();
			controller.setStage(mainStage);
			controller.setApi(Assert.nN(initTask.valueProperty().get()));
			controller.refresh();

		} catch (IOException e) {
			LOG.log(Level.SEVERE, "Error while show the main screen.", e);
		}
	}

}
