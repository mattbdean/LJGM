package net.dean.ljgm.gui;

import java.io.File;

import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ProgressIndicatorBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import net.dean.gui.ImageWithFile;
import net.dean.ljgm.task.Queueable;

/*
 * FullscreenImageView.java
 *
 * Part of project LJGM (Lightweight Java Gallery Manager) (net.dean.ljgm.gui)
 *
 * Originally created on Jun 26, 2013 by Matthew
 */
/**
 * This class allows a progress indicator set to indeterminate progress to
 * be shown until it has been loaded, upon which time the image is displayed.
 */
public class FullScreenImageView extends BorderPane implements Queueable {


	/**
	 * The {@link ProgressIndicator} that shows that the image has not been
	 * loaded yet.
	 */
	private ProgressIndicator progressIndicator;
	
	/** The ImageView that will show the image */
	private ImageView imageView;

	/** The file of the image that will be displayed here. */
	private File file;
	
	/**
	 * Instantiates a new full screen image view.
	 *
	 * @param f The file to use
	 */
	public FullScreenImageView(File f) {
		this.file = f;
		this.progressIndicator = ProgressIndicatorBuilder.create().progress(-1.0).maxWidth(50).build();
		this.imageView = new ImageView();
		setCenter(progressIndicator);
	}
	
	/* (non-Javadoc)
	 * @see net.dean.ljgm.task.Queueable#onLoaded(net.dean.gui.ImageWithFile)
	 */
	@Override
	public void onLoaded(ImageWithFile img) {
		imageView.setImage(img);
		setCenter(imageView);
	}

	/* (non-Javadoc)
	 * @see net.dean.ljgm.task.Queueable#getImageFile()
	 */
	@Override
	public File getImageFile() {
		return file;
	}

}
