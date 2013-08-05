package net.dean.ljgm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class has two purposes: to store a name of a gallery, and to store it's
 * images in the form of a list.
 */
public class Gallery {

	/** The default name. It has a value of {@value #UNKNOWN_NAME}. */
	public static final String UNKNOWN_NAME = "Unknown";

	/**
	 * Represents a <code>null</code> person. The name is equal to "
	 * {@code <null>}"
	 */
	public static final Gallery NULL_GALLERY = new Gallery("<null>", new ArrayList<GallerySource>());

	/** The name of the person. */
	private String name;

	/** A list of files that contain the locations of the person's images. */
	private List<GallerySource> sources;

	/**
	 * Instantiates a new Gallery with no images.
	 * 
	 * @param name
	 *            The name of the Gallery.
	 */
	public Gallery(String name) {
		this(name, new ArrayList<GallerySource>());
	}

	/**
	 * Instantiates a new Gallery.
	 * 
	 * @param name
	 *            The name of the Gallery.
	 * @param sources
	 *            The images of the Gallery.
	 */
	public Gallery(String name, List<GallerySource> sources) {
		this.name = name;
		this.sources = sources;
	}

	/**
	 * Gets the Gallery's images.
	 * 
	 * @return The images of the Gallery.
	 */
	public List<GallerySource> getSources() {
		return sources;
	}

	/**
	 * Sets the images of the Gallery.
	 * 
	 * @param sources
	 *            the new images
	 */
	public void setSources(List<GallerySource> sources) {
		this.sources = sources;
	}
	
	public List<File> getAllImages() {
		List<File> images = new ArrayList<>();
		for (GallerySource source : sources) {
			images.addAll(source.getImagesAsFiles());
		}
		
		return images;
	}

	/**
	 * Gets the name of the Gallery.
	 * 
	 * @return the name of the Gallery.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this Gallery.
	 * 
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// Person [name="Test", images=[image1.png, image2.png]]
		// Gallery [name="Test", sources=[Source [...], Source [...]]
		
		
		StringBuilder str = new StringBuilder("Gallery [name=\"" + name + "\", sources=[");
		for (int i = 0; i < sources.size(); i++) {
			str.append(sources.get(i).toString());

			// If it is not on the last file,
			if (i != sources.size() - 1) {
				// Add a comma and a space to separate the file
				str.append(", ");
				// If it is on the last file,
			} else {
				// Add the closing bracket
				str.append("]");
			}
		}
		// Add the closing tag
		str.append("]");
		return str.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((sources == null) ? 0 : sources.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Gallery other = (Gallery) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sources == null) {
			if (other.sources != null)
				return false;
		} else if (!sources.equals(other.sources))
			return false;
		return true;
	}
}
