package net.dean.ljgm.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderPaneBuilder;
import javafx.scene.layout.FlowPaneBuilder;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import net.dean.ljgm.LJGMUtils;
import net.dean.ljgm.SlideshowProperties;
import net.dean.util.JavaFXUtils;
import net.dean.util.StringUtils;

// TODO: Auto-generated Javadoc
/*
 * SlideshowCreator.java
 *
 * Part of project LJGM (Lightweight Java Gallery Manager) (net.dean.ljgm.gui)
 *
 * Originally created on Jun 25, 2013 by Matthew Dean
 */
/**
 * The Class SlideshowCreator.
 */
public class SlideshowCreator extends Stage {

	/** The properties. */
	private SlideshowProperties properties;

	/**
	 * The Enum OrderType.
	 */
	public static enum OrderType {
		/** The in order. */
		IN_ORDER,
		/** The random. */
		RANDOM
	};

	/** The order. */
	private ComboBox<OrderType> order;

	/** The seconds. */
	private ComboBox<Integer> seconds;

	/** The confirm. */
	private Button confirm;

	/*
	 * Order: (ComboBox) In order, random Duration: (ComboBox) 1-20 seconds
	 */
	/**
	 * Instantiates a new slideshow creator.
	 */
	public SlideshowCreator() {
		super(StageStyle.DECORATED);
		this.confirm = new Button("Start");
		this.properties = new SlideshowProperties();
		setTitle(LJGMUtils.generateStageTitle("Start a slideshow..."));
		this.order = JavaFXUtils.getComboBoxFromEnum(OrderType.class);
		order.getSelectionModel().select(0);
		order.setConverter(new StringConverter<OrderType>() {

			@Override
			public OrderType fromString(String string) {
				System.out.println(string);
				return OrderType.valueOf(string.toUpperCase().replace('_', ' '));
			}

			@Override
			public String toString(OrderType order) {
				// Normalize it
				return StringUtils.asSentence(order.name().replace('_', ' '));
			}
		});
		
		// TODO: Update order and seconds properties

		this.seconds = new ComboBox<>();
		for (int i = 1; i <= 20; i++) {
			seconds.getItems().add(i);
		}
		// Integer will be converted from "1" to "1 second", "2" to "2 seconds", etc.
		seconds.setConverter(new StringConverter<Integer>() {

			@Override
			public Integer fromString(String str) {
				return Integer.parseInt(str.substring(str.indexOf(' ') + 1));
			}

			@Override
			public String toString(Integer i) {
				return i + " second" + (i != 1 ? "s" : "");
			}
		});
		
		// 5 seconds
		seconds.getSelectionModel().select(4);

		BorderPane bp = BorderPaneBuilder.create().padding(new Insets(15)).build();
		GridPane grid = GridPaneBuilder.create().vgap(10).hgap(3).padding(new Insets(5)).build();
		grid.addColumn(0, new Label("Order:"), new Label("Duration:"));
		grid.addColumn(1, order, seconds);

		bp.setCenter(grid);
		bp.setBottom(FlowPaneBuilder.create().alignment(Pos.CENTER).children(confirm).build());

		setScene(new Scene(bp, 225, 125));
	}
	
	public SlideshowProperties getSlideshowProperties() {
		return properties;
	}

	/**
	 * Sets the on confirm.
	 * 
	 * @param handler
	 *            the new on confirm
	 */
	public void setOnConfirm(EventHandler<ActionEvent> handler) {
		confirm.setOnAction(handler);
	}
}
