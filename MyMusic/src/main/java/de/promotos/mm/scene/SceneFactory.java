package de.promotos.mm.scene;

import java.io.IOException;

import javafx.fxml.FXMLLoader;

public class SceneFactory {

	public static final FXMLLoader loadMainScene() throws IOException {
		final FXMLLoader result = new FXMLLoader(SceneFactory.class.getResource("MainScene.fxml"));
		return result;
	}
	
}
