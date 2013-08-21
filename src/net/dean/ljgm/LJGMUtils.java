package net.dean.ljgm;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javafx.stage.Modality;
import javafx.stage.Stage;
import net.dean.gui.fx.ImageWithFile;
import net.dean.util.file.FileUtil;

// TODO: Auto-generated Javadoc
/*
 * LJGMUtils.java
 * 
 * Part of project LJGM (net.dean.ljgm).
 * 
 * Originally created on May 28, 2013 by Matthew Dean.
 *
 */
/**
 * A collection of utility methods commonly used by this project.
 */
public class LJGMUtils {

	/**
	 * Instantiates a new {@link LJGMUtils}.
	 */
	private LJGMUtils() {
		// no instances
	}

	public static boolean isSupportedImage(File f) {
		for (String ext : LJGMDefaults.SUPPORTED_IMAGE_TYPES) {
			if (FileUtil.fileHasExtension(f, ext)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Adjusts the Image to a certain size. If the width is greater than the
	 * height, then it will be resized so that either the width is equal to
	 * <code>maxWidth</code> or height is equal to <code>maxHeight</code>.
	 * 
	 * @param img
	 *            The Image to adjust.
	 * @param maxWidth
	 *            The max width of the new image.
	 * @param maxHeight
	 *            The max height of the new image.
	 * @return A resized version of the given image.
	 */
	public static ImageWithFile adjust(ImageWithFile img, double maxWidth, double maxHeight) {
		// Both under limits, no adjustment needed
		if (img.getWidth() < maxWidth && img.getHeight() < maxHeight) {
			return img;
		}

		// Width and height are the same, set both to the largest, either widht
		// or height
		if (img.getWidth() == img.getHeight()) {
			double size = Math.max(maxWidth, maxHeight);
			return img.resize(size, size);
		}

		// Height is greater than width, resize the image to a width of maxWidth
		// and the original
		// height. Since preserveRatio is true, the Image should be adjusted.
		if (img.getWidth() < img.getHeight()) {
			return img.resize(maxWidth, 0);

			// Width is greater than height. Do the opposite of above.
		} else {
			return img.resize(0, maxHeight);
		}

	}

	/**
	 * Adjusts the Image to a certain size. If the width is greater than the
	 * height, then it will be resized so that either the width or height is
	 * equal to <code>widthHeight</code>.
	 * 
	 * @param img
	 *            The Image to adjust.
	 * @param widthHeight
	 *            The maximum width and height of the image.
	 * @return A resized version of the given image.
	 */
	public static ImageWithFile adjust(ImageWithFile img, double widthHeight) {
		return adjust(img, widthHeight, widthHeight);
	}

	public static List<File> getImagesFrom(File dir, final boolean includeSubdirectories) {
		List<File> files = new ArrayList<File>();
		List<String> relative = getRelativeImagesFrom(dir, includeSubdirectories);

		for (String relativeImage : relative) {
			files.add(new File(dir, relativeImage));
		}

		return files;
	}

	public static List<String> getRelativeImagesFrom(final File dir, final boolean includeSubdirectories) {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("Not a directory: " + dir.getAbsolutePath());
		}

		final Path directory = Paths.get(dir.toURI());
		final List<String> images = new ArrayList<>();

		try {
			Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
					// Check if it's a file first because
					// LJGMUtils.isSupportedImage could throw an exception when
					// given
					// a directory
					if (attrs.isRegularFile()) {

						// If we are including searching through all
						// subdirectories, include all files. If not,
						// only include images directly under the directory.
						boolean shouldUse = (includeSubdirectories) ? true : file.getParent().equals(directory);
						if (LJGMUtils.isSupportedImage(file.toFile()) && shouldUse) {
							// Get the relative path of the visiting file
							images.add(dir.toURI().relativize(file.toUri()).getPath());
						}
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException e) {
					System.err.println("Failed to visit file: " + file.toAbsolutePath() + ": " + e.getLocalizedMessage());
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		return images;
	}

	public static void makeModal(Stage parent, Stage child, Modality modality) {
		child.initOwner(parent);
		child.initModality(modality);

	}

	public static String generateStageTitle(String title) {
		return title + " | " + LJGMDefaults.PROJECT_NAME_SHORT;
	}
}
