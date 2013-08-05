package net.dean.ljgm.gui.gallerycreator;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.scene.layout.HBox;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.dean.ljgm.Gallery;
import net.dean.ljgm.GallerySource;
import net.dean.ljgm.LJGM;
import net.dean.ljgm.LJGMUtils;

/*
 * GalleryCreator.java
 *
 * Part of project LJGM (Lightweight Java Gallery Manager) (net.dean.ljgm.gui)
 *
 * Originally created on Jul 19, 2013 by Matthew Dean
 */
/**
 * This class is used to edit and create Gallery objects.
 */
public class GalleryCreator extends Stage {
	// Universal
	/**
	 * The list of selectors. When a new SourceSelector is added to the list,
	 * the are automatically refreshed
	 */
	private ObservableList<SourceSelector> selectors;

	private ObservableList<SourceSelector> validSelectors;

	/** The scene that will hold the components for this user interface. */
	private Scene mainScene;

	/** The BorderPane that will be the root of the scene. */
	private BorderPane parent;

	// Center
	/**
	 * This VBox holds all the SourceSelectors. However, above the selectors is
	 * a label and below it is {@link #addButtonHBox}.
	 */
	private VBox selectorContainer;

	/**
	 * The scroll pane that is used to let the user view all of the source
	 * selectors when they run off the VBox.
	 */
	private ScrollPane scrollPane;

	/** The button that, when clicked, adds a new source selector to the list. */
	private Button addButton;

	/** The HBox that contains {@link #addButton}. */
	private HBox addButtonHBox;

	// Top
	/** The HBox that contains the name label and the name text field. */
	private HBox nameContainer;

	/** The label that prompts the user for a name for their gallery. */
	private Label nameLabel;

	/**
	 * The name field that will contain the name of the gallery. If this is
	 * empty when the user saves the edits, then the new gallery will have a
	 * name of {@link Gallery#UNKNOWN_NAME}.
	 */
	private TextField nameField;

	// Bottom
	/** The border pane that contains the finish and help buttons. */
	private BorderPane bottomPane;

	/**
	 * The button that, when clicked, saves the edits made to the gallery. This
	 * button is only enabled if there is at least one valid source.
	 */
	private Button finish;

	/** The button that, when clicked, displays a help dialog for the user. */
	private Button help;

	private Gallery editing;

	private boolean isNewGallery;

	/**
	 * Instantiates a new gallery creator.
	 */
	public GalleryCreator() {
		this(null);
	}

	public GalleryCreator(Gallery editing) {
		super(StageStyle.DECORATED);
		this.editing = editing;
		this.isNewGallery = (this.editing == null);

		setTitle(LJGMUtils.generateStageTitle(isNewGallery ? "Create a new Gallery" : "Editing " + this.editing.getName()));

		// ///////// Universal
		this.selectors = FXCollections.observableArrayList();
		selectors.addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				selectorContainer.getChildren().clear();

				selectorContainer.getChildren().add(
						LabelBuilder.create().text("Add and edit sources for your gallery:").style("-fx-font-size: 14;")
								.alignment(Pos.CENTER).build());

				for (SourceSelector selector : selectors) {
					selectorContainer.getChildren().add(selector);
				}
				selectorContainer.getChildren().add(addButtonHBox);
			}
		});

		this.validSelectors = FXCollections.observableArrayList();
		validSelectors.addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable observable) {
				// Enable the finish button if there is more than one valid
				// source
				finish.setDisable(!(validSelectors.size() > 0));
			}
		});

		this.finish = ButtonBuilder.create().text(isNewGallery ? "Create" : "Save")
				.style("-fx-padding: 10 20 10 20; -fx-font-size: 22;").disable(!(validSelectors.size() > 0))
				.onAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						// Remove the old gallery first
						if (!isNewGallery) {
							LJGM.instance().getGalleryManager().getGalleries().remove(GalleryCreator.this.editing);
						}
						LJGM.instance().getGalleryManager().updateGallery(getGalleryFromSelectors());
						GalleryCreator.this.close();
					}
				}).build();

		// ///////// Center
		this.addButton = new Button("", new ImageView(new Image("file:res/add_gallery.png", 16, 16, true, true)));
		addButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				addSelector();
			}
		});
		this.addButtonHBox = HBoxBuilder.create().children(addButton).alignment(Pos.CENTER_RIGHT).build();
		HBox.setMargin(addButton, new Insets(0, 5, 0, 0));
		this.selectorContainer = VBoxBuilder.create().alignment(Pos.CENTER).padding(new Insets(5)).build();
		this.scrollPane = new ScrollPane();
		scrollPane.setContent(selectorContainer);
		scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);

		// ///////// Top
		this.nameLabel = LabelBuilder.create().text("Name:").style("-fx-font-size: 16;").build();
		this.nameField = TextFieldBuilder.create().style("-fx-font-size: 16;").promptText(Gallery.UNKNOWN_NAME)
				.text(isNewGallery ? "" : editing.getName()).build();
		this.nameContainer = HBoxBuilder.create().alignment(Pos.CENTER).padding(new Insets(15))
				.children(nameLabel, nameField).build();
		// Add some padding on the right of the name label to create some
		// space between it and the text field
		HBox.setMargin(nameLabel, new Insets(0, 5, 0, 0));
		// ///////// Bottom

		this.help = new Button("", new ImageView(new Image("file:res/help.png", 33, 33, false, true)));

		this.bottomPane = BorderPaneBuilder.create().left(help).center(finish).padding(new Insets(15)).build();

		// ///////// Put it together
		this.parent = new BorderPane();
		parent.setCenter(scrollPane);
		parent.setTop(nameContainer);
		parent.setBottom(bottomPane);

		this.mainScene = new Scene(parent, 800, 500);

		mainScene.widthProperty().addListener(new InvalidationListener() {

			@Override
			public void invalidated(Observable o) {
				double width = computeContainerWidth();
				addButtonHBox.prefWidthProperty().set(width);
				selectorContainer.prefWidthProperty().set(width);
			}
		});
		addButtonHBox.prefWidthProperty().set(computeContainerWidth());
		selectorContainer.prefWidthProperty().set(computeContainerWidth());

		setScene(mainScene);

		// Add a source selector to get selectors to become invalidated and
		// update the UI
		if (isNewGallery) {
			addSelector();
		} else {
			// Add the gallery's sources to
			for (GallerySource source : editing.getSources()) {
				addSelector(source);
			}
		}
	}

	/**
	 * Computes the appropriate value of the selector container's width.
	 * 
	 * @return The appropriate value of the selector container's width.
	 */
	private double computeContainerWidth() {
		// TODO: Better way to figure this out rather than just "-8"
		return mainScene.widthProperty().doubleValue() - 8;
	}

	private void addSelector() {
		addSelector(null);
	}

	private void addSelector(GallerySource gs) {
		final SourceSelector ss;
		if (gs != null) {
			ss = new SourceSelector(this, gs);
		} else {
			ss = new SourceSelector(this);
		}

		ss.validProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				System.out.println("changed");
				if (newValue) {
					validSelectors.add(ss);
				} else {
					if (validSelectors.contains(ss)) {
						validSelectors.remove(ss);
					}
				}
			}

		});

		selectors.add(ss);
	}

	/**
	 * Removes a selector from the list. If the selector the user is trying to
	 * remove is in the list <b>and</b> is the only selector, the selector is
	 * not removed, only has it's text cleared. Else if the selector is not the
	 * last one in the list, then it will be removed.
	 * 
	 * @param ss
	 *            The source selector to remove
	 */
	void removeSelector(SourceSelector ss) {
		if (!selectors.contains(ss)) {
			throw new IllegalArgumentException("SourceSelector to remove is not in the list: " + ss);
		}

		// Trying to remove the last selector
		if (selectors.size() == 1) {
			// Don't remove it, just clear it.
			ss.reset();
		} else {
			if (validSelectors.contains(ss)) {
				validSelectors.remove(ss);
			}
			// Not the last selector, remove it
			selectors.remove(ss);
		}
	}

	/**
	 * Gathers the data from all the source selectors and the name text field
	 * and makes a Gallery with it.
	 * 
	 * @return A new Gallery with the information from all the source selectors
	 */
	public Gallery getGalleryFromSelectors() {
		List<GallerySource> sources = new ArrayList<>();
		for (SourceSelector selector : selectors) {
			if (!selector.validProperty().get()) {
				throw new IllegalArgumentException("An invalid source has made it into the clean sources list: "
						+ selector.toString());
			}
			// Get the information from the selector and use it to create a new
			// source
			sources.add(new GallerySource(selector.getDirectoryField().getDirectory(), selector.getDirectoryField()
					.getImages(), selector.isWatched(), selector.isIncludeSubdirs()));
		}

		// Determine the name
		String name = nameField.getText();

		// If the name field was empty, then get a new name
		if (name.isEmpty()) {
			// Start with the unknown name
			name = Gallery.UNKNOWN_NAME;

		}

		// Duplicate gallery name prevention:
		int index = 1;

		boolean isDupeName = false;
		for (Gallery g : LJGM.instance().getGalleryManager().getGalleries()) {
			if (name.equals(g.getName())) {
				isDupeName = true;
			}
		}

		if (isDupeName) {
			// Increment the counter to see at what point the name doesn't
			// exist.
			// When it does, use that name.
			boolean sameName = true;
			while (sameName) {
				for (Gallery g : LJGM.instance().getGalleryManager().getGalleries()) {
					// Instead of testing if the gallery's name equals the
					// testing name, test if the gallery's name IS NOT equal to
					// the testing name. that way, as soon as it finds a similar
					// name, it goes to the next testing name.
					if (!g.getName().equals(name)) {
						sameName = false;
					}
				}
				index++;
				name = Gallery.UNKNOWN_NAME + " " + index;
			}
		}

		return new Gallery(name, sources);
	}

	public ObservableList<SourceSelector> getValidSelectors() {
		return validSelectors;
	}
}
