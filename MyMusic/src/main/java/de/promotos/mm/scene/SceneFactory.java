package de.promotos.mm.scene;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class SceneFactory {

	public final Parent loadMainScene() throws IOException {
		return FXMLLoader.load(getClass().getResource("MainScene.fxml"));
	}
	
}
