package net.dean.ljgm.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.SeparatorBuilder;
import javafx.scene.control.Slider;
import javafx.scene.control.SliderBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.scene.layout.FlowPaneBuilder;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.dean.gui.fx.ImageWithFile;
import net.dean.ljgm.Gallery;
import net.dean.ljgm.LJGM;

/*
 * FullscreenView.java
 *
 * Part of project LJGM (Lightweight Java Gallery Manager) (net.dean.ljgm.gui)
 *
 * Originally created on Jun 24, 2013 by Matthew Dean
 *
 */
/**
 * This class is responsible for showing a gallery in full screen.
 * 
 * @author Matthew Dean
 * 
 */
public class FullScreenView extends Stage {

	/** The gallery which is being viewed. */
	private Gallery gallery;

	/** The property that represents the index of the current image. */
	private IntegerProperty imageIndex;

	/** A list of ImageViews that house their own images. */
	private List<ImageView> displays;

	/** The border pane that will house all of the components on the scene. */
	private BorderPane borderPane;

	/** The BorderPane that contains the current ImageView. */
	private BorderPane imageContainer;

	/** The controls that the user can use to navigate the gallery. */
	private FullscreenViewControls controls;

	// private final ImageLoaderQueue queue;

	/**
	 * Instantiates a new FullScreenView
	 * 
	 * @param gallery
	 *            the gallery
	 * @param imageIndex
	 *            the image index
	 */
	public FullScreenView(Gallery gallery, int imageIndex) {
		super(StageStyle.DECORATED);

		this.gallery = gallery;
		this.imageIndex = new SimpleIntegerProperty(imageIndex);
		this.imageIndex.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				controls.slider.valueProperty().set(newValue.intValue() + 1);
			}
		});
		this.controls = new FullscreenViewControls();
		controls.setStyle("-fx-background-color: rgba(215, 215, 215, .9);");
		// this.queue = new ImageLoaderQueue();
		
		List<File> images = this.gallery.getAllImages();
		
		this.displays = new ArrayList<>(images.size());
		for (int i = 0; i < images.size(); i++) {
			displays.add(new ImageView(new ImageWithFile(images.get(i))));
			displays.get(i).setOnKeyReleased(new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent e) {
					if (e.getCode() == KeyCode.LEFT) {
						previous();
					} else if (e.getCode() == KeyCode.RIGHT) {
						next();
					}
				}
			});
			// queue.queue(displays.get(i));
		}
		// queue.start();
		this.borderPane = new BorderPane();
		// Make the background gray
		borderPane.setStyle("-fx-background-color: rgb(215, 215, 215);");
		borderPane.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ESCAPE) {
					FullScreenView.this.close();
				}
			}
		});

		this.imageContainer = new BorderPane();
		setImage(imageIndex);

		BorderPane bp = new BorderPane();
		bp.setCenter(imageContainer);
		borderPane.setCenter(bp);
		//@formatter:off
		borderPane.setBottom(BorderPaneBuilder.create()
				// Set the controls in center
				.center(controls)
				// Set the top to a black, horizontal separator
				.top(SeparatorBuilder.create()
						.orientation(Orientation.HORIZONTAL)
						.style("-fx-background-color: rgb(86, 86, 86)").build())
				.build());
		//@formatter:on

		setScene(new Scene(borderPane));


		// Make the window modal and set the owner to the LJGM stage.
		initModality(Modality.APPLICATION_MODAL);
		initOwner(LJGM.instance().getStage());
		

		// Set full screen
		setFullScreen(false);
	}

	/**
	 * Gets an image at the specified index. If the index is not within the
	 * allowed boundaries (<code>0</code> to
	 * <code>gallery.getImages().size() - 1</code>, then one of two things will
	 * happen. If the index is lower than 0, then it returns
	 * <code>getImage(gallery.getImages().size() - 1)</code>. If it is greater
	 * than <code>getImage(gallery.getImages().size() - 1)</code>, then
	 * <code>getImage(0)</code> is returned. If the index is okay, then it
	 * returns a new ImageWithFile at the specified index.
	 * 
	 * @param index
	 *            The index to use
	 * @return the image
	 */
	public ImageWithFile getImage(int index) {
		if (index > gallery.getAllImages().size() - 1) {
			// If the index is greater than the total amount of images
			// rotate to the first image
			return getImage(0);
		} else if (index < 0) {
			// Else if the index is less than 0 then rotate to the last image
			return getImage(gallery.getAllImages().size() - 1);
		} else {
			// Else the image index is fine and can be loaded as is.
			return new ImageWithFile(this.gallery.getAllImages().get(index), 0, 0, true, true);
		}
	}

	/**
	 * Sets the image at the specified index. If the index is not within the
	 * allowed boundaries (<code>0</code> to
	 * <code>gallery.getImages().size() - 1</code>, then one of two things will
	 * happen. If the index is lower than 0, then it calls
	 * <code>setImage(gallery.getImages().size() - 1)</code>. If it is greater
	 * than <code>getImage(gallery.getImages().size() - 1)</code>, then
	 * <code>setImage(0)</code> is called. If the index is okay, then it
	 * sets the current image to the index.
	 * 
	 * @param index
	 *            the new image
	 */
	public void setImage(int index) {
		List<File> images = gallery.getAllImages();
		// Avoid loading the image twice
		// Check for non-null center because when this method is called (in
		// constructor) getCenter is null and imageIndex's value is 0, so the
		// image is not displayed.
		if (imageContainer.getCenter() != null && index == imageIndex.get()) {
			return;
		}

		if (index > images.size() - 1) {
			// If the index is greater than the total amount of images
			// rotate to the first image
			setImage(0);
			return;
		} else if (index < 0) {
			// Else if the index is less than 0 then rotate to the last image
			setImage(images.size() - 1);
			return;
		} else {
			// Else the image index is fine and can be loaded as is.
			imageIndex.set(index);
			ImageWithFile img = getImage(index);
			imageContainer.setCenter(displays.get(index));
			controls.updateFile(img);
			System.out.println("Setting image to index " + index + " (image " + (index + 1) + ")");
		}
	}

//	/**
//	 * Sets the current image.
//	 * 
//	 * @param image
//	 *            the new image
//	 */
//	public void setImage(ImageWithFile image) {
//		for (int i = 0; i < gallery.getImages().size(); i++) {
//			if (gallery.getImages().get(i).equals(image.getFile())) {
//				// i - 1 to account for the displacement of ordering collections
//				// setImage(i - 1);
//				setImage(i);
//			}
//		}
//	}

	/**
	 * Moves to the next image.
	 */
	public void next() {
		setImage(imageIndex.get() + 1);
	}

	/**
	 * Moves to the previous image.
	 */
	public void previous() {
		setImage(imageIndex.get() - 1);
	}

	/**
	 * Gets the property responsible for handling the image index.
	 * 
	 * @return the image index property.
	 */
	public IntegerProperty imageIndexProperty() {
		return imageIndex;
	}
	
	
	public SlideshowCreator getCreator() {
		return controls.creator;
	}

	/**
	 * This class is meant to be on the bottom of a {@link FullScreenView}. It provides the user with a way to view the images
	 * and see how far along they are.
	 */
	private class FullscreenViewControls extends VBox {

		/** The style of the next and previous buttons. */
		private static final String BUTTON_STYLE = "-fx-font-size: 18px; -fx-font-weight: bold;";

		/** The button that, when pressed, advances to the next image. */
		private Button next;

		/** The button that, when pressed, goes to the previous image. */
		private Button previous;

		/**
		 * The button that, when pressed, shows a dialog that gives options to
		 * start a slideshow.
		 */
		private Button startSlideshow;

		/** The label that shows the image's name. */
		private Label fileNameLabel;

		/** The slider that allows the user to move about the gallery. */
		private Slider slider;

		/** The creator that allows the user to view a slideshow of the gallery. */
		private SlideshowCreator creator;

		/**
		 * Instantiates a new fullscreen view controls.
		 */
		public FullscreenViewControls() {
			setStyle("-fx-background-color: rgba(215, 215, 215, 0.5);");
			this.fileNameLabel = new Label();
			this.creator = new SlideshowCreator();
			creator.setOnConfirm(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					System.out.println("Started");
				}
			});
			// @formatter:off
			this.slider = SliderBuilder.create()
					.max(gallery.getAllImages().size())
					.min(1)
					.value(imageIndex.get() + 1)
					.minorTickCount(0)
					.majorTickUnit(1)
					.snapToTicks(true)
					.showTickLabels(true)
					.showTickMarks(true)
					.blockIncrement(1)
					.build();
			// @formatter:on
			slider.valueProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					System.out.println("Changed: " + oldValue.intValue() + ", " + newValue.intValue());
					setImage(newValue.intValue() - 1);
				}
			});

			slider.setOnKeyPressed(new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent event) {
					if (event.getCode() == KeyCode.RIGHT && slider.valueProperty().intValue() == displays.size()) {
						System.out.println("Very end, next()");
						next();
					}
				}
			});

			EventHandler<ActionEvent> handler = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					if (e.getSource() == next) {
						next();
					} else if (e.getSource() == previous) {
						previous();
					} else if (e.getSource() == startSlideshow) {
						creator.show();
					}
				}
			};
			// 0x2192 = right arrow
			this.next = ButtonBuilder.create().text("" + (char) 0x2192).style(BUTTON_STYLE).onAction(handler).build();
			// 0x2190 = left arrow
			this.previous = ButtonBuilder.create().text("" + (char) 0x2190).style(BUTTON_STYLE).onAction(handler)
					.build();
			this.startSlideshow = ButtonBuilder.create().text("Slideshow").onAction(handler).build();

			next.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					slider.requestFocus();
				}
			});

			setAlignment(Pos.CENTER);
			// setOrientation(Orientation.VERTICAL);
			fileNameLabel.setAlignment(Pos.CENTER);
			getChildren().addAll(fileNameLabel, slider,
					FlowPaneBuilder.create().alignment(Pos.CENTER).children(previous, startSlideshow, next).build());

		}

		/**
		 * Called when the image is changed so that the file name label can be updated
		 * with the appropriate information.
		 * 
		 * @param img
		 *            The new image
		 */
		public void updateFile(ImageWithFile img) {
			fileNameLabel.setText(img.getFile().getName());
		}

	}

}
