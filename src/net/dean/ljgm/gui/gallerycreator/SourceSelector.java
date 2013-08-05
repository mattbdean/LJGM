package net.dean.ljgm.gui.gallerycreator;

import java.io.File;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleButtonBuilder;
import javafx.scene.control.TooltipBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.DirectoryChooser;
import net.dean.ljgm.GallerySource;
import net.dean.ljgm.LJGMDefaults;

// TODO: Auto-generated Javadoc
/**
 * This class is used to graphically represent a {@link GallerySource}.
 */
class SourceSelector extends HBox {

	/**
	 * The button that determines if this source is watched or not. If this
	 * button is selected, then the {@link #changeImages} and
	 * {@link #includeSubdirs} is enabled.
	 */
	private ToggleButton watched;

	/** The button that determines if this source will include subdirectories */
	private ToggleButton includeSubdirs;

	/**
	 * The directory field that will show the user what directory/images they
	 * have choosen
	 */
	private DirectoryTextField directoryField;

	/**
	 * The button that, when clicked, shows a {@link DirectoryChooser} to let
	 * the user specify the base directory for this source.
	 */
	private Button changeDirectory;

	/**
	 * The button that, when clicked, shows an {@link ImagePicker} to let the
	 * user specify images of an unwatched source. This button is disabled if
	 * {@link #watched} is selected.
	 */
	private Button changeImages;

	/**
	 * The button that, when pressed, will try to remove this source selector
	 * from the list. If this is the only selector left, however,
	 * {@link #reset()} will be called.
	 */
	private Button remove;

	/**
	 * The directory chooser which will let the user choose the base directory
	 * for this source.
	 */
	private DirectoryChooser directoryChooser;

	/** The property that represents whether this source is valid or not. */
	private BooleanProperty valid;

	/** The gallery creator that made this selector. */
	private GalleryCreator galleryCreator;
	
	private ObjectProperty<InvalidationListener> fieldsChanged;

	/**
	 * Instantiates a new source selector.
	 * 
	 * @param galleryCreator
	 *            the gallery creator The creator of this object
	 */
	public SourceSelector(GalleryCreator galleryCreator) {
		this(galleryCreator, null);
	}

	public SourceSelector(GalleryCreator galleryCreator, GallerySource gs) {
		super(3);
		this.galleryCreator = galleryCreator;
		this.fieldsChanged = new SimpleObjectProperty<>();
		setAlignment(Pos.CENTER);
		// Padding on left and right, average of 5 on top/bottom
		setPadding(new Insets(0, 5, 7, 5));

		this.galleryCreator.getScene().widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldVal, Number newVal) {
				prefWidthProperty().set(newVal.doubleValue() - 8);
			}
		});
		this.valid = new SimpleBooleanProperty(false);
		this.watched = new ToggleButton("Watched?");
		watched.setStyle("-fx-base: " + LJGMDefaults.BLUE + ";");
		watched.setTooltip(TooltipBuilder
				.create()
				.prefWidth(300)
				.wrapText(true)
				.text("Rather than specifying individual images, you can make this source \'watched\'. This means that every"
						+ " time the gallery is loaded, all the images in this directory will be listed.").build());
		watched.selectedProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable o) {
				updateImageButton();
				updateValidity();
				includeSubdirs.setDisable(!watched.selectedProperty().get());
			}
		});
		this.includeSubdirs = ToggleButtonBuilder.create().text("Include Subdirectories?").style("-fx-base: " + LJGMDefaults.BLUE + ";").disable(true).build();
		includeSubdirs.setTooltip(TooltipBuilder.create().text("Include searching in subdirectories in a watched source")
				.build());

		// Create the directory chooser
		this.directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Browse for a directory");

		// TODO May add a folder icon here
		this.changeDirectory = new Button("Directory");
		changeDirectory.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				File dir = directoryChooser.showDialog(SourceSelector.this.galleryCreator.getOwner());
				if (dir != null) {
					// The user actually selected a directory/didn't hit the cancel button
					directoryField.setDirectory(directoryChooser.showDialog(SourceSelector.this.galleryCreator.getOwner()));
					updateValidity();
				}
				
				
			}
		});
		this.changeImages = ButtonBuilder.create().text("Images").disable(true).onAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				ImagePicker picker = new ImagePicker(directoryField.getDirectory());
				picker.showAndWait();
				directoryField.setImages(picker.getRelativeFileNames());
				updateValidity();
			}
		}).build();

		this.remove = new Button("", new ImageView(new Image("file:res/remove_directory.png", 16, 16, true, true)));
		remove.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				SourceSelector.this.galleryCreator.removeSelector(SourceSelector.this);
			}
		});
				
		this.directoryField = new DirectoryTextField(this);
		directoryField.setPromptText("Browse for a directory");
		directoryField.prefWidth(Double.MAX_VALUE);
		directoryField.maxWidth(Double.MAX_VALUE);
		HBox.setHgrow(directoryField, Priority.ALWAYS);
		// Is a new source
		if (gs != null) {
			directoryField.setDirectory(gs.getDirectory());
			if (!gs.isWatched()) {
				directoryField.setImages(gs.getImages());
			}
			watched.setSelected(gs.isWatched());
			includeSubdirs.setSelected(gs.isIncludeSubdirectories());
		}		

		
		getChildren().addAll(watched, includeSubdirs, directoryField, changeDirectory, changeImages, remove);

		updateValidity();
		if (valid.get()) {
			galleryCreator.getValidSelectors().add(this);
		}
	}

	/**
	 * Makes this source selector look like one that was just created.
	 */
	public void reset() {
		directoryField.reset();
		watched.setSelected(false);
		includeSubdirs.setDisable(true);
	}

	public BooleanProperty validProperty() {
		return valid;
	}

	/**
	 * Checks if the source is watched.
	 * 
	 * @return True, if {@link #watched} is selected.
	 */
	public boolean isWatched() {
		return watched.isSelected();
	}

	/**
	 * Checks if the source includes subdirectories.
	 * 
	 * @return True, if {@link #includeSubdirs} is selected.
	 */
	public boolean isIncludeSubdirs() {
		return includeSubdirs.isSelected();
	}

	/**
	 * Updates the image button to enable or disable it based on the values of
	 * {@link #directoryField} and {@link #isWatched()}.
	 */
	public void updateImageButton() {
		if (directoryField.isValidDirectory() && !isWatched()) {
			changeImages.setDisable(false);
		} else {
			changeImages.setDisable(true);
		}
	}

	/**
	 * Updates the validity of the source.
	 */
	void updateValidity() {
//		System.out.println("BEFORE update: Valid source? " + valid + "; Valid dir? " + directoryField.isValidDirectory() + "; valid images? " + directoryField.isValidImages() + "; text not empty? " + directoryField.getText().isEmpty());
		if (isWatched()) {
			// Watched sources only need a valid directory
			valid.set(directoryField.isValidDirectory() && !directoryField.getText().isEmpty());
//			valid.set(true);
		} else {
			// Non-watched sources need a valid directory and valid images
			valid.set(directoryField.isValidDirectory() && directoryField.isValidImages());
		}

//		System.out.println("AFTER update Valid source? " + valid + "; Valid dir? " + directoryField.isValidDirectory() + "; valid images? " + directoryField.isValidImages() + "; text not empty? " + directoryField.getText().isEmpty());
	}
	
	void setOnSourceChanged(InvalidationListener listener) {
		fieldsChanged.set(listener);
	}
	
	ObjectProperty<InvalidationListener> onFieldsChanged() {
		return fieldsChanged;
	}

	/**
	 * Gets the directory field.
	 * 
	 * @return the directory field
	 */
	public DirectoryTextField getDirectoryField() {
		return directoryField;
	}
}