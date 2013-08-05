package net.dean.ljgm.task;

import java.io.File;

import net.dean.gui.ImageWithFile;

// TODO: Auto-generated Javadoc
/*
 * Queueable.java
 *
 * Part of project LJGM (Lightweight Java Gallery Manager) (net.dean.ljgm.task)
 *
 * Originally created on Jun 26, 2013 by Matthew
 */
/**
 * The Interface Queueable.
 */
public interface Queueable {
	
	/**
	 * On loaded.
	 *
	 * @param img the img
	 */
	public abstract void onLoaded(ImageWithFile img);
	
	/**
	 * Gets the image file.
	 *
	 * @return the image file
	 */
	public abstract File getImageFile();
}
