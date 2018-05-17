package de.promotos.mm.scene;

import javafx.scene.image.Image;

public class ImageFactory {

	private ImageFactory() {}
	
	public final static Image getAppIcon() {
		return new Image(ImageFactory.class.getResourceAsStream("/image/Icon.png"));
	}

	public static final Image getSplashImage() {
		return new Image(ImageFactory.class.getResourceAsStream("/image/Splash.png"));
	}
}
