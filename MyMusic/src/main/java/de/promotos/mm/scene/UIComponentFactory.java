package de.promotos.mm.scene;

import java.io.File;

import javafx.stage.FileChooser;

/**
 * Helper class to build common ui components
 * 
 * @author Promotos
 *
 */
public final class UIComponentFactory {

	/**
	 * Build a file chooser for audio files.
	 * 
	 * @return The predefined file chooser component.
	 */
	public final static FileChooser buildAudioFileChooser() {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select audio file to upload");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

		final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("mp3 files (*.mp3)", "*.mp3");
		fileChooser.getExtensionFilters().add(extFilter);

		return fileChooser;
	}

}
