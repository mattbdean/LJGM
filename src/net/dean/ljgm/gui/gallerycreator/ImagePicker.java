/*
 * 
 */
package net.dean.ljgm.gui.gallerycreator;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPaneBuilder;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.stage.Stage;
import net.dean.ljgm.LJGMDefaults;
import net.dean.ljgm.LJGMUtils;
import net.dean.util.CollectionUtils;
import net.dean.util.JavaFXUtils;

/*
 * ImagePicker.java
 *
 * Part of project LJGM (Lightweight Java Gallery Manager) (net.dean.ljgm.gui.gallerycreator)
 *
 * Originally created on Jul 21, 2013 by Matthew
 */
/**
 * This class shows the user files and folders on a grid and allows the user to
 * pick the images they want for a source.
 */
class ImagePicker extends Stage {

	/** The amount of columns in {@link #imageHolder}. */
	private static final int COLUMNS = 3;

	/**
	 * The amount of vertical and horizontal spacing between buttons in
	 * {@link #imageHolder}.
	 */
	private static final int SPACING = 7;

	/** The amount of padding for {@link #imageHolder}. */
	private static final int PADDING = 15;

	/** The base directory. */
	private File baseDirectory;

	/** The list of relative file names. */
	private ObservableList<String> relativeFileNames;

	/** The scene upon which everything is placed. */
	private Scene scene;

	/** The label that shows how many images are selected. */
	private Label selectedImagesLabel;

	/** The button that closes the stage. */
	private Button finish;

	/** The button that toggles all the visible toggle buttons when clicked. */
	private Button toggleAll;

	/** The button that selects all the visible toggle buttons when clicked. */
	private Button selectAll;

	/** The button that deselects all the visible toggle buttons when clicked. */
	private Button deselectAll;

	/**
	 * The button that brings the user up one directory. This is disabled if it
	 * is already at the base directory.
	 */
	private Button upFolder;

	/** The grid pane that holds all the images and folders. */
	private GridPane imageHolder;

	/** The current relative folder. */
	private String currentRelativeFolder;

	/**
	 * Instantiates a new image picker.
	 * 
	 * @param baseDirectory
	 *            the base directory
	 */
	public ImagePicker(final File baseDirectory) {
		super();
		if (!baseDirectory.isDirectory()) {
			throw new IllegalArgumentException("Not a directory: " + baseDirectory.getAbsolutePath());
		}

		this.baseDirectory = baseDirectory;
		this.relativeFileNames = FXCollections.observableArrayList();
		relativeFileNames.addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				selectedImagesLabel.setText("Selected images: " + relativeFileNames.size());
			}
		});
		this.currentRelativeFolder = "";

		this.selectedImagesLabel = new Label("Selected images: " + relativeFileNames.size());
		this.finish = ButtonBuilder.create().text("Done").alignment(Pos.CENTER).style("-fx-font-size: 15")
				.onAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent e) {
						// Got all the images, can close the stage so that the
						// GalleryCreator
						// can receive them.
						ImagePicker.this.close();
					}
				}).build();

		EventHandler<ActionEvent> buttonControlHandler = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				ObservableList<Node> nodes = imageHolder.getChildren();
				for (Node node : nodes) {
					if (node instanceof ImageButton) {
						ImageButton button = (ImageButton) node;
						if (e.getSource() == toggleAll) {
							// Toggle the buttons
							button.setSelected(!button.isSelected());
						} else if (e.getSource() == selectAll) {
							// Select it if it is not already
							if (!button.isSelected()) {
								button.setSelected(true);
							}
						} else if (e.getSource() == deselectAll) {
							// Deselect it if it is not already
							if (button.isSelected()) {
								button.setSelected(false);
							}
						}
					}
				}
			}

		};
		this.toggleAll = ButtonBuilder.create().onAction(buttonControlHandler).text("Toggle All").build();
		this.selectAll = ButtonBuilder.create().onAction(buttonControlHandler).text("Select All").build();
		this.deselectAll = ButtonBuilder.create().onAction(buttonControlHandler).text("Deselect All").build();
		this.upFolder = ButtonBuilder.create().text("Up").onAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				System.out.println(currentRelativeFolder);
				// Is at the top of the base directory
				if (currentRelativeFolder.equals("")) {
					// Root of the base directory
					return;
				}

				String upLevel;

				// Get the amount of slashes in the relative folder
				int slashes = 0;
				int index = 0;
				while ((index = currentRelativeFolder.indexOf(File.separator, index + 1)) != -1) {
					slashes++;
				}

				// Only one slash, upper directory must be the root for the base
				if (slashes == 1) {
					upLevel = "";
				} else {
					// More than one slash
					upLevel = currentRelativeFolder.substring(0,
							currentRelativeFolder.lastIndexOf(File.separator, currentRelativeFolder.length() - 2));
				}

				setDirectory(upLevel);
			}
		}).build();

		this.imageHolder = GridPaneBuilder.create().vgap(SPACING).hgap(SPACING).padding(new Insets(PADDING)).build();
		// Add column constraints to the image holder
		JavaFXUtils.constrainColumnsEvenly(COLUMNS, imageHolder);
		BorderPane imageBp = BorderPaneBuilder.create().center(imageHolder).build();
		VBox topPane = VBoxBuilder.create().alignment(Pos.CENTER).children(selectedImagesLabel, upFolder).build();

		// Make a border pane whose center is the finish button, and the right
		// is an HBox that contains
		// the mass image control buttons (toggleAll, selectAll, and
		// deselectAll).
		HBox massControlButtons = HBoxBuilder.create().children(selectAll, toggleAll, deselectAll).spacing(5)
				.padding(new Insets(5)).build();
		BorderPane bottomPane = BorderPaneBuilder.create().center(finish).right(massControlButtons).padding(new Insets(8))
				.build();

		ScrollPane pane = ScrollPaneBuilder.create().content(imageBp).build();

		// Main border pane
		BorderPane bp = new BorderPane();
		bp.setCenter(pane);
		bp.setTop(topPane);
		bp.setBottom(bottomPane);
		this.scene = new Scene(bp, 650, 650);
		scene.widthProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable o) {
				imageHolder.maxWidthProperty().set(scene.widthProperty().get() - (COLUMNS / SPACING) - PADDING);
			}
		});

		imageHolder.maxWidthProperty().set(scene.widthProperty().get() - (COLUMNS / SPACING) - PADDING);
		// Set the directory to the base folder
		setDirectory("");
		setScene(scene);
	}

	/**
	 * Sets the relative directory.
	 * 
	 * @param relativeFolder
	 *            the new directory
	 */
	private void setDirectory(String relativeFolder) {
		final File folder = new File(baseDirectory, relativeFolder);
		if (!folder.isDirectory()) {
			throw new IllegalArgumentException("Not a directory: " + folder.getAbsolutePath());
		}

		this.currentRelativeFolder = relativeFolder;
		// Only add the file separator if it is not the root of the base
		// directory.
		// Otherwise it could cause problems on UNIX systems.
		if (!relativeFolder.equals("")) {
			this.currentRelativeFolder += File.separator;
		}

		// At base directory
		if (currentRelativeFolder.equals("")) {
			upFolder.setDisable(true);
		} else {
			upFolder.setDisable(false);
		}

		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				imageHolder.getChildren().clear();
				File[] directories = folder.listFiles(new FileFilter() {

					@Override
					public boolean accept(File pathname) {
						return pathname.isDirectory();
					}
				});
				File[] images = folder.listFiles(new FileFilter() {

					@Override
					public boolean accept(File pathname) {
						if (pathname.isDirectory()) {
							// Auto deny
							return false;
						}
						return LJGMUtils.isSupportedImage(pathname);
					}
				});

				// Use ButtonBase because it's the direct superclass of Button
				// and ToggleButton
				List<ButtonBase> buttons = new ArrayList<>();

				for (int i = 0; i < directories.length; i++) {
					buttons.add(new DirectoryButton(directories[i].getName()));
				}
				for (int i = 0; i < images.length; i++) {
					buttons.add(new ImageButton(images[i].getName()));
				}

				// Less than COLUMNS buttons, don't split the list of lists
				// except if you want an exception
				List<List<ButtonBase>> columns = new ArrayList<List<ButtonBase>>(1);
				columns.add(buttons);
				if (COLUMNS < buttons.size()) {
					// Safe to separate
					columns.clear();
					columns = CollectionUtils.separate(buttons, COLUMNS);
				}

				// Add the columns to the
				for (int i = 0; i < columns.size(); i++) {
					imageHolder.addColumn(i, CollectionUtils.toArray(columns.get(i), ButtonBase.class));
				}
			}
		});
	}

	/**
	 * Gets the relative file names.
	 * 
	 * @return the relative file names
	 */
	public List<String> getRelativeFileNames() {
		return relativeFileNames;
	}

	/**
	 * This class represents a directory. When clicked on, it sets the relative
	 * directory to itself.
	 */
	private class DirectoryButton extends Button {

		/**
		 * The icon that will be used for this button
		 */
		private final ImageView icon = new ImageView(new Image("file:res/folder.png", 16, 16, true, true));

		/**
		 * Instantiates a new directory button.
		 * 
		 * @param name
		 *            the name
		 */
		public DirectoryButton(final String name) {
			super(name);
			setGraphic(icon);
			setTooltip(new Tooltip(name));
			setStyle("-fx-base: " + LJGMDefaults.BLUE + ";");
			setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent e) {
					setDirectory(currentRelativeFolder + name);
				}
			});
		}
	}

	/**
	 * This class represents an image. When selected, the image's relative path
	 * is added to {@link ImagePicker#relativeFileNames}, and removed when
	 * unselected.
	 */
	private final class ImageButton extends ToggleButton {

		/** The image's relative path. */
		private final String relativePath;

		/**
		 * Instantiates a new image button.
		 * 
		 * @param name
		 *            the name
		 */
		public ImageButton(final String name) {
			setText(name);
			setTooltip(new Tooltip(name));
			if (relativeFileNames.contains(currentRelativeFolder + name)) {
				setSelected(true);
			}
			this.relativePath = currentRelativeFolder + name;

			// Use the selected property instead of setOnAction(...) because
			// when the toggleAll button or any other mass image control button
			// is pressed it does not count as an action. By using the selected
			// property it catches both of those events
			selectedProperty().addListener(new InvalidationListener() {

				@Override
				public void invalidated(Observable o) {
					if (isSelected()) {
						relativeFileNames.add(relativePath);
					} else {
						relativeFileNames.remove(relativePath);
					}
				}
			});
		}
	}
}
