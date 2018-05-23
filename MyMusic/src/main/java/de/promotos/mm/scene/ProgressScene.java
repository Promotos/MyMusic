package de.promotos.mm.scene;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.promotos.mm.service.ErrorRunnable;
import javafx.animation.FadeTransition;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Progress scene shown to the user for long running tasks.
 * 
 * @author Promotos
 *
 */
public class ProgressScene extends VBox {

	private static final Logger LOG = Logger.getLogger(ProgressScene.class.getName());
	private static final String APP_TITLE = "Working";

	private final Pane splashLayout;
	private final ProgressBar loadProgress;
	private final Label progressText;
	private static final double SPLASH_WIDTH = 150;
	private static final double SPLASH_HEIGHT = 227;

	/**
	 * Create a new instance of the splash scene.
	 */
	public ProgressScene() {
		loadProgress = new ProgressBar();
		progressText = new Label("...");
		splashLayout = new VBox();
	}

	/**
	 * Show the splash scene.
	 * 
	 * @param initStage
	 *           Stage instance to draw the splash scene content on.
	 * @param task
	 *           The task to be executed while the splash scene is displayed.
	 * @param completionHandler
	 *           The task executed on completion of the worker task.
	 * @param errorHandler
	 *           The callback executed if an error on the worker task occur.
	 */
	public void show(final Stage initStage, final Task<?> task, final Runnable completionHandler,
			final ErrorRunnable errorHandler) {
		init();

		initStage.setTitle(APP_TITLE);
		initStage.getIcons().add(ImageFactory.getAppIcon());
		progressText.textProperty().bind(task.messageProperty());
		loadProgress.progressProperty().bind(task.progressProperty());

		task.exceptionProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				final Exception ex = (Exception) newValue;
				LOG.log(Level.SEVERE, "Error while executing task.", ex);
				errorHandler.onError(ex);
			}
		});

		task.stateProperty().addListener((observableValue, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				loadProgress.progressProperty().unbind();
				loadProgress.setProgress(1);
				initStage.toFront();
				FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
				fadeSplash.setFromValue(1.0);
				fadeSplash.setToValue(0.0);
				fadeSplash.setOnFinished(actionEvent -> initStage.hide());
				fadeSplash.play();

				completionHandler.run();
			}
		});

		Scene splashScene = new Scene(splashLayout, Color.TRANSPARENT);
		final Rectangle2D bounds = Screen.getPrimary().getBounds();
		initStage.setScene(splashScene);
		initStage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
		initStage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
		initStage.initStyle(StageStyle.TRANSPARENT);
		initStage.show();
	}

	private void init() {
		final ImageView splash = new ImageView(ImageFactory.getSplashImage());

		loadProgress.setPrefWidth(SPLASH_WIDTH - 20);
		progressText.setAlignment(Pos.CENTER);

		splashLayout.getChildren().addAll(splash, loadProgress, progressText);
		splashLayout.setStyle("-fx-padding: 5; "
				+ "-fx-border-width:5; "
				+ "-fx-border-color: "
				+ "linear-gradient("
				+ "to bottom, "
				+ "gray, "
				+ "derive(gray, 50%)" + ");");
		splashLayout.setEffect(new DropShadow());
	}
}
