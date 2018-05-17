package de.promotos.mm;
import java.io.IOException;

import de.promotos.mm.scene.ImageFactory;
import de.promotos.mm.scene.MainSceneController;
import de.promotos.mm.scene.SceneFactory;
import de.promotos.mm.scene.SplashScene;
import de.promotos.mm.service.DriveApi;
import de.promotos.mm.service.InitTaskFactory;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MyMusicApp extends Application {

	private final String APP_TITLE = "MyMusic - Cloud Music Player";
	private Stage mainStage;
	private MainSceneController controller;
	
	private DriveApi api;

	public static void main(String[] args) {
		MyMusicApp.launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		final Task<DriveApi> initTask = new InitTaskFactory().get();
		new SplashScene().show(primaryStage, initTask, () -> showMainScene(initTask));
		new Thread(initTask).start();
	}
	
	private void showMainScene(Task<DriveApi> initTask) {
		try {
			api = initTask.valueProperty().get();
			
	        mainStage = new Stage(StageStyle.DECORATED);
	        mainStage.setTitle(APP_TITLE);
	        mainStage.getIcons().add(ImageFactory.getAppIcon());

	        final FXMLLoader loader = SceneFactory.loadMainScene();
	        mainStage.setScene(new Scene(loader.load()));
	        mainStage.show();

	        controller = loader.getController();
	        controller.update("Hallo");
	        
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
