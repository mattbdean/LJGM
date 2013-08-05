package net.dean.ljgm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.dean.parsers.ini.AssignmentMode;
import net.dean.parsers.ini.CommentMode;
import net.dean.parsers.ini.IniElement;
import net.dean.parsers.ini.IniFile;
import net.dean.parsers.ini.IniFileFactory;
import net.dean.parsers.ini.IniFileTransformer;
import net.dean.parsers.ini.IniSyntaxException;
import net.dean.parsers.ini.Section;

/**
 * This class is responsible for the configuration of the application.
 */
public class ConfigManager {

	/**
	 * The location of the file from which the settings of this application will
	 * be parsed.
	 */
	private static final File SETTINGS_FILE = new File("settings.ini");

	/**
	 * The default properties of this application.
	 */
	private static final IniFile DEFAULT;

	/**
	 * The {@link IniFile} that will be used to read and write to the file.
	 */
	private IniFile iniFile;

	/**
	 * The {@link IniFileTransformer} that will be used to save the.
	 * {@link IniFile}.
	 */
	private IniFileTransformer transformer;

	/**
	 * Instantiates a new {@link ConfigManager}.
	 */
	public ConfigManager() {
		this.transformer = new IniFileTransformer(AssignmentMode.EQUALS, CommentMode.POUND_SIGN, true, true);

		if (SETTINGS_FILE.exists()) {
			try {
				this.iniFile = new IniFileFactory().build(SETTINGS_FILE);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (IniSyntaxException e) {
				e.printStackTrace();
			}
		} else {
			this.iniFile = DEFAULT;
		}

	}

	/**
	 * Gets the IniFile that will be used to read and write to the configuration
	 * file on the disk.
	 * 
	 * @return The ini file
	 */
	public IniFile getIniFile() {
		return iniFile;
	}

	/**
	 * Gets a property from a section.
	 * 
	 * @param sectionName
	 *            The name of the section in which the desired value is located
	 * @param key
	 *            The key which will return the desired value
	 * @return The value of the property with the given key in the given
	 *         section.
	 */
	public String get(String sectionName, String key) {
		return iniFile.getSection(sectionName).get(key);
	}

	/**
	 * Sets a value of a key in a certain section.
	 * 
	 * @param sectionName
	 *            The name of the section
	 * @param key
	 *            The key
	 * @param value
	 *            The value
	 */
	public void set(String sectionName, String key, String value) {
		iniFile.getSection(sectionName).set(key, value);
	}

	/**
	 * Gets a property under the <code>AdvancedSettings</code> section.
	 * 
	 * @param key
	 *            The key to use
	 * @return A value of the key that is in the <code>AdvancedSettings</code>
	 *         section.
	 */
	public String getAdvanced(String key) {
		return get("AdvancedSettings", key);
	}

	/**
	 * Gets a property under the <code>ImageProperties</code> section.
	 * 
	 * @param key
	 *            The key to use
	 * @return A value of the key that is in the <code>ImageProperties</code>
	 *         section.
	 */
	public String getImageProperty(String key) {
		return get("ImageProperties", key);
	}

	/**
	 * Saves the file to {@link #SETTINGS_FILE}.
	 */
	public void save() {
		transformer.export(iniFile, SETTINGS_FILE);
	}

	// Image properties

	/**
	 * Gets the value of <code>icon.smooth</code> under the
	 * <code>ImageProperties</code> section.
	 * 
	 * @return The value that represents <code>icon.smooth</code>
	 */
	public boolean isIconSmooth() {
		return Boolean.valueOf(getImageProperty("icon.smooth"));
	}

	/**
	 * Gets the value of <code>icon.preserve_ratio</code> under the
	 * <code>ImageProperties</code> section.
	 * 
	 * @return The value that represents <code>icon.preserve_ratio</code>
	 */
	public boolean isIconPreserveRatio() {
		return Boolean.valueOf(getImageProperty("icon.preserve_ratio"));
	}

	/**
	 * Gets the value of <code>icon.width</code> under the
	 * <code>ImageProperties</code> section.
	 * 
	 * @return The value that represents <code>icon.width</code>
	 */
	public double getIconWidth() {
		return Double.valueOf(getImageProperty("icon.width"));
	}

	/**
	 * Gets the value of <code>icon.height</code> under the
	 * <code>ImageProperties</code> section.
	 * 
	 * @return The value that represents <code>icon.height</code>
	 */
	public double getIconHeight() {
		return Double.valueOf(getImageProperty("icon.height"));
	}

	/**
	 * Gets the value of <code>full.smooth</code> under the
	 * <code>ImageProperties</code> section.
	 * 
	 * @return The value that represents <code>full.smooth</code>
	 */
	public boolean isFullscreenImagesSmooth() {
		return Boolean.valueOf(getImageProperty("full.smooth"));
	}

	/**
	 * Gets the value of <code>full.preserve_ratio</code> under the
	 * <code>ImageProperties</code> section.
	 * 
	 * @return The value that represents <code>full.preserve_ratio</code>
	 */
	public boolean isFullscreenImagesPreserveRatio() {
		return Boolean.valueOf(getImageProperty("full.preserve_ratio"));
	}

	// Advanced properties

	/**
	 * Gets the value of <code>debug_mode</code> under the
	 * <code>AdvancedSettings</code> section.
	 * 
	 * @return The value that represents <code>debug_mode</code>
	 */
	public boolean isDebug() {
		return Boolean.valueOf(getAdvanced("debug_mode"));
	}

	/**
	 * Gets the value of <code>background_threads</code> under the
	 * <code>AdvancedSettings</code> section.
	 * 
	 * @return The value that represents <code>background_threads</code>
	 */
	public int getBackgroundThreads() {
		return Integer.valueOf(getAdvanced("background_threads"));
	}

	static {
		DEFAULT = new IniFileFactory().newIniFile();
		List<IniElement> imageProperties = new ArrayList<>();
		imageProperties.add(new IniElement("icon.smooth", String.valueOf(LJGMDefaults.ICON_SMOOTH), new String[] {
				"Image icons are rendered smoothly. May take a toll on the application if",
				"many icons are to be rendered." }));
		imageProperties.add(new IniElement("icon.preserve_ratio", String.valueOf(LJGMDefaults.ICON_PRESERVE_RATIO),
				"Icons preserve their original ratio."));
		imageProperties
				.add(new IniElement("icon.width", String.valueOf(LJGMDefaults.ICON_WIDTH), "Maximum icon width."));
		imageProperties.add(new IniElement("icon.height", String.valueOf(LJGMDefaults.ICON_HEIGHT),
				"Maximum icon height."));
		imageProperties.add(new IniElement("full.smooth", String.valueOf(LJGMDefaults.FULLSCREEN_SMOOTH),
				"Fullscreen images are rendered smoothly."));
		imageProperties.add(new IniElement("full.preserve_ratio", String
				.valueOf(LJGMDefaults.FULLSCREEN_PRESERVE_RATIO), "Fullscreen images preserve their original ratio."));

		DEFAULT.add(new Section("ImageProperties", imageProperties));

		List<IniElement> advanced = new ArrayList<>();
		advanced.add(new IniElement("debug_mode", String.valueOf(LJGMDefaults.DEBUG_MODE), new String[] {
				"Debug mode is enabled. Enables the DEBUG messages to be outputed", "through the console." }));
		advanced.add(new IniElement(
				"background_threads",
				String.valueOf(LJGMDefaults.BACKGROUND_THREADS),
				new String[] { "The amount of background threads that will be used to render", "icons. Recommended 5." }));
		DEFAULT.add(new Section("AdvancedSettings", advanced));
	}
}
