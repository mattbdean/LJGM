package net.dean.ljgm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.dean.util.file.FileUtil;

/*
 * GallerySource.java
 *
 * Part of project LJGM (Lightweight Java Gallery Manager) (net.dean.ljgm)
 *
 * Originally created on Jul 20, 2013 by Matthew
 */
/**
 * This class defines a source for images of a Gallery. It's primary function is
 * to retrieve a set of images from a directory statically or dynamically
 * depending on if this source is watched. When retrieving the images for this
 * source, there are a few possible outcomes. If the directory is watched, this
 * class will look for files with specific image-related extensions
 * (specifically, the ones in {@link FileUtil#IMAGE_EXTENSIONS}) in the
 * directory. If the directory is not watched, then this class will return a
 * list of files <b>all relative to the directory</b> through a list of file
 * names.
 */
public class GallerySource {

	/** A list of file names relative to the directory */
	private List<String> images;

	/** The directory which serves as the base for this source. */
	private File directory;

	/**
	 * Defines whether or not to include all files inside the directory instead
	 * of certain ones. If this is true, the image list is not used at all.
	 */
	private boolean watched;

	/**
	 * Defines whether or not to look in subdirectories to find images for this
	 * source. This only applies if this source is watched.
	 */
	private boolean includeSubdirectories;

	/**
	 * Instantiates a new watched GallerySource with a given directory that
	 * doesn't include it's subdirectories
	 * 
	 * 
	 * @param directory
	 *            The base directory for this source
	 */
	public GallerySource(File directory) {
		this(directory, false);
	}

	/**
	 * Instantiates a new watched GallerySource with a given directory.
	 * 
	 * @param directory
	 *            The base directory for this source
	 * @param includeSubdirectories
	 *            Whether to include subdirectories for this source
	 */
	public GallerySource(File directory, boolean includeSubdirectories) {
		this(directory, new ArrayList<String>(), true, includeSubdirectories);
	}

	/**
	 * Instantiates a new GallerySource that isn't watched.
	 * 
	 * @param directory
	 *            The base directory for this source
	 * @param images
	 *            The list of files that will be used for this source
	 */
	public GallerySource(File directory, List<String> images) {
		this(directory, images, false, false);
	}

	/**
	 * Instantiates a new GallerySource.
	 * 
	 * @param directory
	 *            The base directory for this source
	 * @param images
	 *            The list of files that will be used if this source is not
	 *            watched.
	 * @param watched
	 *            Defines if this source is watched.
	 * @param includeSubdirectories
	 *            Defines if this source will search for images in the
	 *            directory's subdirectories.
	 */
	public GallerySource(File directory, List<String> images, boolean watched, boolean includeSubdirectories) {
		this.directory = directory;
		this.images = images;
		this.watched = watched;
		this.includeSubdirectories = includeSubdirectories;
	}

	public List<String> getImages() {
		return images;
	}

	/**
	 * Gets the images for this GallerySource. If this source is watched, then
	 * this method will return a list of all the image files directly under the
	 * directory, and optionally in it's subdirectories too. If the source is
	 * not watched, then it will return a list of files from the image list
	 * whose parent directory is this source's directory.
	 * 
	 * @return the images This source's images
	 */
	public List<File> getImagesAsFiles() {
		final List<File> imageList = new ArrayList<>();

		if (!watched) {
			// Not watched, so we can use the file names given to us
			for (String image : images) {
				imageList.add(new File(directory, image));
			}
		} else {
			// Is watched; add all images from directory
			imageList.addAll(LJGMUtils.getImagesFrom(directory, includeSubdirectories));
		}

		return imageList;
	}

	/**
	 * Sets the base directory of this source.
	 * 
	 * @param directory
	 *            the new directory
	 * @throws IllegalArgumentException
	 *             If the given file is not a directory
	 */
	public void setDirectory(File directory) {
		if (!directory.isDirectory()) {
			throw new IllegalArgumentException("Not a directory: " + directory.getAbsolutePath());
		}

		this.directory = directory;
	}
	

	/**
	 * Gets the directory.
	 * 
	 * @return the directory
	 */
	public File getDirectory() {
		return directory;
	}

	/**
	 * Sets the list of image file names.
	 * 
	 * @param images
	 *            the new images
	 */
	public void setImages(List<String> images) {
		this.images = images;
	}

	/**
	 * Checks if this source is watched.
	 * 
	 * @return True, if it is watched
	 */
	public boolean isWatched() {
		return watched;
	}

	/**
	 * Sets if the directory is watched.
	 * 
	 * @param watched
	 *            If the directory is watched.
	 */
	public void setWatched(boolean watched) {
		this.watched = watched;
	}

	/**
	 * Checks if this source includes subdirectories.
	 * 
	 * @return True, if this source includes subdirectories
	 * @see #includeSubdirectories
	 */
	public boolean isIncludeSubdirectories() {
		return includeSubdirectories;
	}

	/**
	 * Sets if the source includes subdirectories.
	 * 
	 * @param includeSubdirectories
	 *            If the source includes subdirectories
	 * @see #includeSubdirectories
	 */
	public void setIncludeSubdirectories(boolean includeSubdirectories) {
		this.includeSubdirectories = includeSubdirectories;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((directory == null) ? 0 : directory.hashCode());
		result = prime * result + ((images == null) ? 0 : images.hashCode());
		result = prime * result + (includeSubdirectories ? 1231 : 1237);
		result = prime * result + (watched ? 1231 : 1237);
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
		GallerySource other = (GallerySource) obj;
		if (directory == null) {
			if (other.directory != null)
				return false;
		} else if (!directory.equals(other.directory))
			return false;
		if (images == null) {
			if (other.images != null)
				return false;
		} else if (!images.equals(other.images))
			return false;
		if (includeSubdirectories != other.includeSubdirectories)
			return false;
		if (watched != other.watched)
			return false;
		return true;
	}
	
//	@Override
//	public int hashCode() {
//		int hash = 0;
//		if (images != null) 
//			hash += images.hashCode();
//		
//		hash = (includeSubdirectories ? )
//			
//		return hash;
//	}
	

}
