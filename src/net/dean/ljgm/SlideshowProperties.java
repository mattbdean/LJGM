package net.dean.ljgm;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import net.dean.ljgm.gui.SlideshowCreator;
import net.dean.ljgm.gui.SlideshowCreator.OrderType;

// TODO: Auto-generated Javadoc
/*
 * SlideshowProperties.java
 *
 * Part of project LJGM (Lightweight Java Gallery Manager) (net.dean.ljgm)
 *
 * Originally created on Jun 26, 2013 by Matthew
 */
/**
 * The Class SlideshowProperties.
 */
public class SlideshowProperties {
	
	/** The image duration. */
	private ObjectProperty<Integer> imageDuration;
	
	/** The order. */
	private ObjectProperty<SlideshowCreator.OrderType> order;
	
	/**
	 * Instantiates a new slideshow properties.
	 */
	public SlideshowProperties() {
		this.imageDuration = new SimpleObjectProperty<Integer>(5);
		this.order = new SimpleObjectProperty<SlideshowCreator.OrderType>(OrderType.IN_ORDER);
	}
	
	/**
	 * Image duration property.
	 *
	 * @return the object property
	 */
	public ObjectProperty<Integer> imageDurationProperty() {
		return imageDuration;
	}
	
	/**
	 * Order type property.
	 *
	 * @return the object property
	 */
	public ObjectProperty<OrderType> orderTypeProperty() {
		return order;
	}
}
