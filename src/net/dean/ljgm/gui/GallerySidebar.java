package net.dean.ljgm.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import net.dean.ljgm.Gallery;
import net.dean.ljgm.GalleryManager;
import net.dean.ljgm.LJGM;
import net.dean.ljgm.LJGMUtils;
import net.dean.ljgm.gui.gallerycreator.GalleryCreator;

/**
 * This class is designed to graphically show the galleries in the library file (
 * <code>library.xml</code>). If a gallery is clicked on, then the gallery's
 * pictures are shown on a {@link ViewingArea}.
 */
public class GallerySidebar extends BorderPane {

	private ListView<String> galleryList;
	/**
	 * The {@link GalleryManager} that lets this class populate it's list with
	 * elements.
	 */
	private GalleryManager galleryManager;

	/** The {@link ViewingArea} that will show the selected gallery's pictures. */
	private ViewingArea view;
	
	/**
	 * The button that brings up a new window to create a gallery when clicked.
	 */
	private Button addGalleryButton;

	/**
	 * Instantiates a new {@link GallerySidebar}.
	 * 
	 * @param galleryManager
	 *            The {@link GalleryManager} that will allow this class to
	 *            populate it's contents with elements representing galleries.
	 * @param view
	 *            The {@link ViewingArea} that will show the pictures of the
	 *            selected gallery.
	 */
	public GallerySidebar(GalleryManager galleryManager, final ViewingArea view) {
		super();
		this.galleryManager = galleryManager;
		this.view = view;
		this.galleryList = new ListView<>();
		this.addGalleryButton = new Button("Add Gallery", new ImageView(new Image("file:res/add_gallery.png", 25, 25, true, false)));
		BorderPane.setAlignment(addGalleryButton, Pos.CENTER);
		BorderPane.setMargin(addGalleryButton, new Insets(5, 0, 0, 0));

		galleryList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		populate(galleryManager.getGalleries());

		galleryList.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.getButton() == MouseButton.PRIMARY) {
					GallerySidebar.this.view.setFocus(getSelectedGallery());
				} else if (e.getButton() == MouseButton.SECONDARY) {
					ContextMenu menu = new ContextMenu();
					menu.getItems().add(MenuItemBuilder.create().text("Edit...").onAction(new EventHandler<ActionEvent>() {

						@Override
						public void handle(ActionEvent event) {
							GalleryCreator creator = new GalleryCreator(getSelectedGallery());
							LJGMUtils.makeModal(LJGM.instance().getStage(), creator, Modality.APPLICATION_MODAL);
							creator.show();
						}
					}).build());

					menu.show(LJGM.instance().getStage(), e.getScreenX(), e.getScreenY());
				}
				
			}
		});
		
		addGalleryButton.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent e) {
				new GalleryCreator().show();
			}
		});
		setCenter(galleryList);
		setBottom(addGalleryButton);
	}
	
	public ListView<String> getListView() {
		return galleryList;
	}

	/**
	 * Populates the list with the names of the galleries.
	 * 
	 * @param galleries
	 *            The galleries whose names will populate this component.
	 */
	public void populate(List<Gallery> galleries) {
		List<String> names = new ArrayList<String>();

		for (Gallery p : galleries) {
			names.add(p.getName());
		}

		Collections.sort(names);

		List<String> newNames = new ArrayList<>(names.size());
		for (String name : names) {
			
			// [17] Test gallery
			newNames.add("[" + galleryManager.getGallery(name).getAllImages().size() + "] " + name);
		}
		galleryList.setItems(FXCollections.observableList(newNames));
	}

	/**
	 * Gets the selected gallery.
	 * 
	 * @return The selected gallery. If none is selected, <code>null</code> is
	 *         returned.
	 */
	public Gallery getSelectedGallery() {
		if (galleryList.getSelectionModel().getSelectedItem() == null) {
			return null;
		}
		return galleryManager.getGallery(removeImageCount(galleryList.getSelectionModel().getSelectedItem()));
	}

	/**
	 * Removes the image count prefix. For instance, <code>[74] Bob Jones</code>
	 * will return <code>Bob Jones</code>.
	 * 
	 * @param imgCount
	 *            The String that has the image count prefix.
	 * @return A version of the given string without the image count prefix.
	 */
	public String removeImageCount(String imgCount) {
		return imgCount.substring(imgCount.indexOf("]") + 2);
	}
}
