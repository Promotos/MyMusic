package de.promotos.mm.scene;

import javafx.scene.image.Image;

/**
 * Factory to access built-in image resources.
 * 
 * @author Promotos
 *
 */
public class ImageFactory {

	private ImageFactory() {
	}

	/**
	 * The application icon.
	 * 
	 * @return The image instance of the application icon.
	 */
	public static final Image getAppIcon() {
		return new Image(ImageFactory.class.getResourceAsStream("/image/Icon.png"));
	}

	/**
	 * The splash screen image.
	 * 
	 * @return The instance of the splash screen image.
	 */
	public static final Image getSplashImage() {
		return new Image(ImageFactory.class.getResourceAsStream("/image/Splash.png"));
	}
}
