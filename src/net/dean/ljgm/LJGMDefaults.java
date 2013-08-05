package net.dean.ljgm;


// TODO: Auto-generated Javadoc
/*
 * ImageConstants.java
 * 
 * Part of project Pic2Face (net.dean.pic2face).
 * 
 * Originally created on May 27, 2013 by Matthew.
 *
 */
/**
 * A collection of static fields that will be the standards of the project.
 */
public class LJGMDefaults {

	public static final String[] SUPPORTED_IMAGE_TYPES = {"jpg", "jpeg", "png", "gif"};
	
	/**
	 * The default value that defines whether or not the loaded images will be
	 * smooth. This is equal to {@value #ICON_SMOOTH}.
	 */
	public static final boolean ICON_SMOOTH = true;

	/**
	 * The default value that defines whether or not fullscreen images should be
	 * rendered smoothly. This is equal to {@value #FULLSCREEN_SMOOTH}.
	 */
	public static final boolean FULLSCREEN_SMOOTH = true;

	/**
	 * The default value that defines whether or not the loaded images will
	 * preserve their ratios. This is equal to {@value #ICON_PRESERVE_RATIO}.
	 */
	public static final boolean ICON_PRESERVE_RATIO = true;

	/**
	 * The default value that defines whether or not fullscreen images should
	 * preserve their ratios.
	 */
	public static final boolean FULLSCREEN_PRESERVE_RATIO = true;

	/**
	 * The default value that defines the maximum width of an icon of an image.
	 * This is equal to {@value #ICON_WIDTH}
	 */
	public static final double ICON_WIDTH = 100.0;

	/**
	 * The default value that defines the maximum height of an icon of an image.
	 * This is equal to {@value #ICON_HEIGHT}
	 */
	public static final double ICON_HEIGHT = 100.0;

	/**
	 * The default value that defines amount of background threads that will be
	 * available to load images. This is equal to {@value #BACKGROUND_THREADS}
	 */
	public static final int BACKGROUND_THREADS = 5;

	/**
	 * The name of the project. It's value is {@value #PROJECT_NAME}.
	 */
	public static final String PROJECT_NAME = "Lightweight Java Gallery Manager";
	
	/**
	 * The short name of the project. It's value is {@value #PROJECT_NAME}.
	 */
	public static final String PROJECT_NAME_SHORT = "LJGM";
	

	/**
	 * The version of this application. It's value is {@value #PROJECT_VERSION}.
	 */
	public static final String PROJECT_VERSION = "0.8_2 alpha";

	/**
	 * The default value that defines whether to log certain events and to show
	 * certain certain UI elements.
	 */
	public static final boolean DEBUG_MODE = true;
	
	public static final String BLUE = "#0D88BA";
}
