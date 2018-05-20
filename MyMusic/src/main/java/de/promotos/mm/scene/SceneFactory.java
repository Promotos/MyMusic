package de.promotos.mm.scene;

import javafx.fxml.FXMLLoader;

public class SceneFactory {

	private SceneFactory() {}
	
	public static final FXMLLoader loadMainScene() {
		return new FXMLLoader(SceneFactory.class.getResource("MainScene.fxml"));
	}
	
}
