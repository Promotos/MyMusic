package de.promotos.mm.scene;

import javafx.fxml.FXMLLoader;

/**
 * Factory to load the FXML files.
 * 
 * @author Promotos
 *
 */
public class SceneFactory {

	private SceneFactory() {
	}

	/**
	 * Load the main scene.
	 * 
	 * @return The loader instance to access the scene and controller.
	 */
	public static final FXMLLoader loadMainScene() {
		return new FXMLLoader(SceneFactory.class.getResource("MainScene.fxml"));
	}

}
