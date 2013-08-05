package net.dean.ljgm.task;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import net.dean.ljgm.LJGM;
import net.dean.ljgm.LJGMDefaults;
import net.dean.ljgm.gui.ImageDisplay;
import net.dean.ljgm.gui.StatusBar;

// TODO: Auto-generated Javadoc
/**
 * This class is responsible for queuing {@link ImageDisplay} objects for
 * loading their images. Once {@link #queue(ImageDisplay)} is called, the
 * {@link ImageDisplay} is put into a list of queued displays. If there busy
 * workers, then a separate thread calls {@link #pollReadyWorkers()}.<br>
 * <br>
 * This basic process is followed:
 * <ol>
 * <li>An {@link ImageDisplay} is queued.
 * <li>If a service is available, it starts working. If one is not, then it will
 * sit in the queue.
 * <li>If at this point the {@link ImageDisplay} is still in the queue, it is
 * removed.
 * <li>Once the worker is finished, it passes on the image to the ImageDisplay.
 * <li>The service is declared ready for a new task.
 * </ol>
 * 
 * @author Matthew Dean
 * 
 */
public class ImageLoaderQueue {

	/** A list of the load times of the last loaded gallery. */
	private List<Long> loadTimes;

	/** A list of all the services. */
	private final List<ImageLoaderService> services;

	/** A {@link DoubleProperty} representing the progress of the image loading. */
	private SimpleDoubleProperty progressProperty;

	/** The amount of total images. */
	private long totalImages;

	/** The amount of loaded images. */
	private long loadedImages;

	/** A list of all the available services. */
	private final List<ImageLoaderService> availableServices;
	
	private boolean stopped;

	/**
	 * A list of all the Queueables that are not being worked on by a worker.
	 */
	private List<Queueable> queuedDisplays;

	/**
	 * Instantiates a new {@link ImageLoaderQueue}.
	 */
	public ImageLoaderQueue() {
		services = new ArrayList<>();
		for (int i = 0; i < LJGMDefaults.BACKGROUND_THREADS; i++) {
			services.add(new ImageLoaderService(this));
		}
		availableServices = new ArrayList<>(services);
		this.queuedDisplays = new ArrayList<>();
		this.progressProperty = new SimpleDoubleProperty(0);
		this.loadTimes = new ArrayList<>();
		this.totalImages = 0;
		this.loadedImages = 0;
		this.stopped = true;
	}

	/**
	 * Starts the {@link ServiceDeployer}.
	 */
	public void start() {
		stopped = false;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				LJGM.instance().getLogger().debug("Assigning all available services");

				// Check for a higher amount of queued images than available
				// workers.
				// If so, then don't iterate through all the services because
				// that would cause
				// an IndexOutOfBoundsException.
				List<ImageLoaderService> availableServices = getAvailableServices();
				int amountWorkersNeeded = (queuedDisplays.size() > availableServices.size() ? availableServices.size()
						: queuedDisplays.size());

				for (int i = amountWorkersNeeded; i > 0; i--) {
					ImageLoaderService service = availableServices.get(i - 1);
					if (queuedDisplays.get(i - 1) != null) {
						service.assignJob(queuedDisplays.get(i - 1));
					}
				}
			}

		});
	}

	/**
	 * Cancels all running services.
	 */
	public void cancel() {
		if (stopped) {
			LJGM.instance().getLogger().warn("ImageLoaderQueue was not running when the cancel signal was given.");
		} else {
			// Stop services from getting new jobs in the finished(...) method
			stopped = true;
		}
		
		reset();
	}

	/**
	 * Resets this {@link ImageLoaderQueue}. This method resets the services,
	 * queued displays, load times, the loaded images counter, and the total
	 * images counter.
	 */
	private void reset() {
		LJGM.instance().getLogger().debug("Resetting queue...");
	
		// Clear the list of available services.
		availableServices.clear();
		// Cancel the services if they are scheduled and reset them to
		// State.READY.
		resetServices();
		// Services are guaranteed to be ready, add them to the list
		availableServices.addAll(services);
		// Reset the queued
		queuedDisplays.clear();
		// Reset the load times
		loadTimes.clear();
	
		loadedImages = 0;
		totalImages = 0;
	}

	/**
	 * Cancels and resets all the services.
	 */
	private void resetServices() {
//		for (ImageLoaderService service : services) {
		for (int i = 0; i < services.size(); i++) {
			ImageLoaderService service = services.get(i);
			System.out.println("State is " + service.getState());
			System.out.println("Cancelling service.");
			service.cancel();
		}
		
		for (int i = 0; i < services.size(); i++) {
			ImageLoaderService service = services.get(i);
			System.out.println("State is " + service.getState());
			System.out.println("Resetting service.");
			service.reset();
			System.out.println("State is " + service.getState());
		}
		
	}

	/**
	 * Checks if all the queued images have been loaded.
	 * 
	 * @return True, {@link #totalImages} is equal to {@link #loadedImages}.
	 */
	public boolean isDone() {
		return totalImages == loadedImages;
	}

	/**
	 * Called when a worker has finished it's job. First, the
	 *
	 * @param service The worker that has finished it's job.
	 * {@link ImageDisplay} who's image the service was working on is added to
	 * it. Then, if the queue is not empty, it assigns the service another
	 * {@link ImageDisplay} to work on. Finally, it updates the
	 * {@link StatusBar} to show how many images have been loaded.
	 */
	void finished(ImageLoaderService service) {
		if (stopped) {
			// Don't do anything after the stop signal has been given.
			// Don't want to waste any more valuable memory.
			return;
		}
		
		/*
		 * Call the display's imageLoaded() method
		 */
		service.getJobTarget().onLoaded(service.imageProperty().get());
		loadedImages++;
		progressProperty.set((double) loadedImages / totalImages);
		if (!queuedDisplays.isEmpty()) {
			service.assignJob(queuedDisplays.get(0));
			loadTimes.add(service.getLoadTime());
			queuedDisplays.remove(0);

			// @formatter:off
			LJGM.instance() .getStatusBar() .setMessage("Loading images.. (" + getLoadedImages() + "/"
									+ getTotalImages() + ")" + ((LJGM.instance().getConfigManager()
											.isDebug()) ? ", average " + getAverageLoadTime() + "ms" : ""));
			// @formatter:on
		}
	}

	/**
	 * Gets a list of services whose state is equal to {@link State#READY}.
	 * 
	 * @return A {@link List} of available workers whose
	 *         <code>stateProperty()</code> is equal to {@link State#READY}.
	 * 
	 * @see State
	 * @see Worker
	 */
	private List<ImageLoaderService> getAvailableServices() {
		return availableServices;
	}

	/**
	 * Queues an ImageDisplay to be loaded by a service.
	 * 
	 * @param queueable
	 *            the img disp
	 */
	public void queue(Queueable queueable) {
		queuedDisplays.add(queueable);
		totalImages++;
	}

	/**
	 * Gets the {@link DoubleProperty} that represents the progress of loading
	 * the images.
	 * 
	 * @return the read only double property
	 */
	public ReadOnlyDoubleProperty progressProperty() {
		return progressProperty;
	}

	/**
	 * Gets the amount of total images.
	 * 
	 * @return The amount of total images.
	 */
	public long getTotalImages() {
		return totalImages;
	}

	/**
	 * Gets the amount of loaded images.
	 * 
	 * @return The amount of loaded images
	 */
	public long getLoadedImages() {
		return loadedImages;
	}

	/**
	 * Gets the average image load time.
	 * 
	 * @return The average image load time.
	 */
	public long getAverageLoadTime() {
		long total = 0;
		for (long l : loadTimes) {
			total += l;
		}

		if (total == 0) {
			return 0;
		}
		return total / loadTimes.size();
	}
	
	/**
	 * Checks if the queued displays list is empty.
	 * 
	 * @return True, if is empty
	 */
	public boolean isEmpty() {
		return queuedDisplays.isEmpty();
	}

}
