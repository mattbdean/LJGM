package net.dean.ljgm.gui;

import java.io.File;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ProgressIndicatorBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import net.dean.gui.fx.ImageWithFile;
import net.dean.ljgm.Gallery;
import net.dean.ljgm.LJGM;
import net.dean.ljgm.task.Queueable;

/**
 * This class displays an icon of an image once it has been loaded. If the image
 * has not been loaded yet, a {@link ProgressIndicator} with indeterminate
 * progress will show in it's place. Once it's image has been loaded,
 * {@link #onLoaded(ImageWithFile)} can be called and the ProgressIndicator will
 * be replaced.
 * 
 * @see ProgressIndicator#INDETERMINATE_PROGRESS.
 */
public class ImageDisplay extends BorderPane implements Queueable {

	/**
	 * The {@link ProgressIndicator} that shows that the image has not been
	 * loaded yet.
	 */
	private ProgressIndicator progressIndicator;

	/** The file of the image that will be displayed here. */
	private File file;

	/** The button that will be used to detect mouse presses. */
	private Button button;

	/**
	 * Instantiates a new {@link ImageDisplay}.
	 * 
	 * @param f
	 *            The file to use to construct the Image.
	 * @param owner
	 *            The owner of the image
	 * @param index
	 *            The index of the image in the gallery
	 */
	public ImageDisplay(File f, final Gallery owner, final int index) {
		this.file = f;
		double prefSize = 65;
		this.progressIndicator = ProgressIndicatorBuilder.create().progress(-1.0).prefWidth(prefSize).prefHeight(prefSize)
				.build();
		this.button = ButtonBuilder.create().alignment(Pos.CENTER).textAlignment(TextAlignment.CENTER)
				.contentDisplay(ContentDisplay.TOP).graphic(progressIndicator).build();
		// setAlignment(Pos.CENTER);
		setPrefSize(125, 125);
		setCenter(button);
		if (LJGM.instance().getConfigManager().isDebug()) {
			button.setText(index + "; " + file.getName());
		}

		EventHandler<MouseEvent> click = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				if (!(e.getButton() == MouseButton.PRIMARY)) {
					return;
				}

				FullScreenView view = new FullScreenView(owner, index);
				view.setImage(index);
				view.show();
			}
		};
		button.setOnMouseClicked(click);
		// This is necessary because if the user clicks on the progress
		// indicator while it's still loading, then the image will not be
		// displayed.
		progressIndicator.setOnMouseClicked(click);
	}

	/**
	 * Gets the progress indicator.
	 * 
	 * @return The progress indicator
	 */
	public ProgressIndicator getProgressIndicator() {
		return progressIndicator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.dean.ljgm.task.Queueable#onLoaded(net.dean.gui.fx.ImageWithFIle)
	 */
	public void onLoaded(ImageWithFile img) {
		button.setGraphic(new ImageView(img));
	}

	/**
	 * Gets the image file.
	 * 
	 * @return the image file
	 */
	public File getImageFile() {
		return file;
	}
}
