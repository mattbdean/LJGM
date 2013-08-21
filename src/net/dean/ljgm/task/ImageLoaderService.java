package net.dean.ljgm.task;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import net.dean.gui.fx.ImageWithFile;
import net.dean.ljgm.LJGM;
import net.dean.ljgm.LJGMDefaults;
import net.dean.ljgm.gui.ImageDisplay;

// TODO: Auto-generated Javadoc
/**
 * This class is responsible for starting a background thread to load an image.
 */
public class ImageLoaderService extends Service<ImageWithFile> {
	
	/** The start time. */
	private long startTime;
	
	/** The end time. */
	private long endTime;
	
	/** The time it took to load the last image. */
	private long loadTime;

	/** The image that will be returned. */
	private ObjectProperty<ImageWithFile> image;

	/** The Queueable whose image will be loaded. */
	private Queueable queueable;
	
	/** The {@link ImageLoaderQueue} who is in charge of this service. */
	private ImageLoaderQueue imageLoaderQueue;

	/**
	 * Instantiates a new {@link ImageLoaderService}.
	 *
	 * @param imageLoaderQueue The ImageLoaderQueue
	 */
	public ImageLoaderService(final ImageLoaderQueue imageLoaderQueue) {
		this.image = new SimpleObjectProperty<ImageWithFile>();
		this.imageLoaderQueue = imageLoaderQueue;
		this.queueable = null;
		setOnSucceeded(new EventHandler<WorkerStateEvent>() {

			@Override
			public void handle(WorkerStateEvent wse) {
				queueable.onLoaded(image.get());
				endTime = System.currentTimeMillis();
				loadTime = (endTime - startTime);
				LJGM.instance().getLogger().debug("Loaded file \"" + image.get().getFile().getName() + "\" in " + loadTime + "ms. Average " + imageLoaderQueue.getAverageLoadTime() + "ms.");
				reset();
				ImageLoaderService.this.imageLoaderQueue.finished(ImageLoaderService.this);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.concurrent.Service#createTask()
	 */
	@Override
	protected Task<ImageWithFile> createTask() {
		return new Task<ImageWithFile>() {
			protected ImageWithFile call() {
				ImageWithFile i = new ImageWithFile(queueable.getImageFile(), LJGMDefaults.ICON_WIDTH,
						LJGMDefaults.ICON_HEIGHT, true, true);
				image.set(i);
				return image.get();
			}
		};
	}

	/**
	 * Assigns an {@link ImageDisplay} for this service to work on and starts
	 * the service.
	 * 
	 * @param queueable
	 *            The ImageDisplay whose image will be loaded by this service.
	 */
	public void assignJob(Queueable queueable) {
		this.queueable = queueable;
		start();
	}
	
	/* (non-Javadoc)
	 * @see javafx.concurrent.Service#start()
	 */
	@Override
	public void start() {
		startTime = System.currentTimeMillis();
		super.start();
	}

	/**
	 * Gets the {@link Queueable} whose image will be loaded by this service.
	 * 
	 * @return The {@link Queueable}.
	 */
	public Queueable getJobTarget() {
		return queueable;
	}

	/**
	 * Assigns an {@link ImageDisplay} for this service to work on.
	 * 
	 * @param imgDisp
	 *            The new target
	 */
	public void setJobTarget(ImageDisplay imgDisp) {
		this.queueable = imgDisp;
	}

	/**
	 * The property assigned to the loaded {@link ImageWithFile} that is loaded
	 * by this service.
	 * 
	 * @return The {@link ObjectProperty} that wraps the {@link ImageWithFile}.
	 */
	public ObjectProperty<ImageWithFile> imageProperty() {
		return image;
	}
	
	/**
	 * Gets the time it took to load the last image.
	 *
	 * @return The load time.
	 */
	public long getLoadTime() {
		return loadTime;
	}

}
