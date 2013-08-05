package net.dean.ljgm.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import net.dean.ljgm.Gallery;
import net.dean.ljgm.LJGM;
import net.dean.ljgm.LJGMUtils;
import net.dean.ljgm.task.ImageLoaderQueue;
import net.dean.util.CollectionUtils;

// TODO: Auto-generated Javadoc
/**
 * This class responsible for giving a small display of the images of a.
 * 
 * {@link Gallery}.
 * 
 * @author Matthew Dean
 */
public class ViewingArea extends BorderPane {

	/** The amount of columns of pictures. */
	private static final int COLUMNS = 5;

	/** The width of each picture that will be displayed on this component. */
	public static final double ICON_WIDTH = 75;

	/**
	 * The amount of space between each picture and the amount of space from the
	 * edges of the window.
	 */
	private static final double PADDING = 20;

	/** The {@link Gallery} which this component is displaying pictures for. */
	private Gallery focus;

	/** The image queue. */
	private ImageLoaderQueue imageQueue;

	/** The grid. */
	private GridPane grid;

	/**
	 * Instantiates a new ViewingArea that shows a message to the user that says
	 * that to get started, they need to create a new user.
	 * 
	 * @param ljgm
	 *            the ljgm
	 */
	public ViewingArea(LJGM ljgm) {
		this(ljgm, null);
	}

	/**
	 * Instantiates a new ViewingArea with a given focus.
	 * 
	 * @param ljgm
	 *            The ljgm
	 * @param g
	 *            The default focus of this {@link ViewingArea}.
	 */
	public ViewingArea(LJGM ljgm, Gallery g) {
		this.focus = g;
		this.imageQueue = new ImageLoaderQueue();
		this.grid = GridPaneBuilder.create().padding(new Insets(PADDING)).hgap(PADDING).vgap(PADDING).build();

		LJGM.instance().getStatusBar().bindProgress(imageQueue.progressProperty());

		if (g == null) {
			setCenter(getBlankDisplay("There are no galleries!"));
			return;
		} else {
			setFocus(g);
			return;
		}
	}

	/**
	 * Sets the focus of this component. The Gallery's images are loaded into a
	 * grid of images and added to the image queue.
	 * 
	 * @param focus
	 *            The person which images will be displayed here.
	 */
	public void setFocus(final Gallery focus) {
		if (focus == null) {
			// If the person is null then all that will be performed is the
			// removing of the images
			LJGM.instance().getLogger().throwable(new NullPointerException("Cannot set the focus to a null person."));
			return;
		}

		// Optimized by not loading the same person if it is clicked
		// again.

		// Catch the NullPointerException by created if the focus has not been
		// set yet
		if (!(this.focus == null)) {
			// Since no two galleries have the same name, this method
			// prevents reloading galleries unnecessarily when they are
			// clicked.
			if (this.focus.getName().equals(focus.getName())) {
				return;
			}
		}

		if (focus.getAllImages().isEmpty()) {
			setCenter(getBlankDisplay("There are no images in this gallery!"));
			return;
		}

		grid.getChildren().clear();

		// Will be true when the ViewingArea is created or when the user adds a
		// new gallery when there were none previously or the user switches from
		// a gallery that had no images
		if (centerProperty().isNotEqualTo(grid).get()) {
			setCenter(grid);
		}

		List<File> allImages = focus.getAllImages();
		LJGM.instance().getLogger().info("Loading " + allImages.size() + " images for person \"" + focus.getName() + "\"");
		LJGM.instance().getStage().setTitle(LJGMUtils.generateStageTitle(focus.getName()));
		
		// If the image queue is still working on loading some other gallery,
		// cancel it since it is not needed anymore; this is more important
		if (!imageQueue.isDone()) {
			imageQueue.cancel();
		}

		// Separate the list of files into sublists lists of rows
		final List<List<File>> rows;
		if (allImages.size() > COLUMNS) {
			// Need for more than one row
			rows = CollectionUtils.separate(allImages, (int) Math.ceil((double) allImages.size() / COLUMNS));
		} else {
			rows = new ArrayList<>();
			List<File> row = new ArrayList<>();
			row.addAll(allImages);
			rows.add(row);
		}

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				LJGM.instance().getStatusBar().show();

				// Iterate through the rows
				for (int i = 0; i < rows.size(); i++) {
					// Add an ImageDisplay for every image
					for (int j = 0; j < rows.get(i).size(); j++) {
						// Add it to (j, i)
						ImageDisplay imgDisp = new ImageDisplay(rows.get(i).get(j), focus, ((i * COLUMNS) + j));
						grid.add(imgDisp, j, i);
						imageQueue.queue(imgDisp);
					}
				}

				// Start the deployer
				imageQueue.start();
				// When it is finished, the fully loaded grid will be added.
			}
		});

		this.focus = focus;
	}

	/**
	 * Gets a blank panel with a with a given message in the center.
	 * 
	 * @param message
	 *            The message to use
	 * @return A new VBox with a Label in the center.
	 */
	private VBox getBlankDisplay(String message) {
		VBox pane = new VBox();
		pane.setAlignment(Pos.CENTER);

		Label messageText = new Label(message);
		messageText.setAlignment(Pos.CENTER);
		pane.getChildren().add(messageText);

		VBox.setVgrow(messageText, Priority.ALWAYS);
		return pane;
	}
}