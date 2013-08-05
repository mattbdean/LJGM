package net.dean.ljgm.gui;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.FlowPane;

// TODO: Auto-generated Javadoc
/**
 * This class is responsible for showing the user what is going on in the
 * background and it's status.
 */
public class StatusBar extends FlowPane {

	/** The {@link ProgressBar} that will be used to show progress. */
	private ProgressBar progressBar;

	/** The {@link Label} that will show a message to the user. */
	private Label messageLabel;

	/**
	 * Instantiates a new StatusBar.
	 */
	public StatusBar() {
		this("");
	}

	/**
	 * Instantiates a new StatusBar with a given message.
	 * 
	 * @param message
	 *            The default message.
	 */
	public StatusBar(String message) {
		this(message, 0.0);
	}

	/**
	 * Instantiates a new StatusBar with a given message and a given initial
	 * progress of the {@link ProgressBar}.
	 * 
	 * @param message
	 *            The default message.
	 * @param initialProgress
	 *            The initial progress of the {@link ProgressBar}.
	 */
	public StatusBar(String message, double initialProgress) {
		super(3, 0);
		this.messageLabel = new Label(message);
		this.progressBar = new ProgressBar(initialProgress);
		// progressBar.setPrefWidth(500);
		progressBar.progressProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue.doubleValue() == 1.0) {
					hide();
				}
			}
		});
		hide();
		getChildren().addAll(messageLabel, progressBar);
		setPadding(new Insets(3, 2, 3, 2));
	}

	/**
	 * Hides the message label and the progress bar.
	 */
	public void hide() {
		messageLabel.visibleProperty().set(false);
		progressBar.visibleProperty().set(false);
	}

	/**
	 * Shows the message label and the progress bar.
	 */
	public void show() {
		messageLabel.visibleProperty().set(true);
		progressBar.visibleProperty().set(true);
	}

	/**
	 * Sets the message.
	 * 
	 * @param message
	 *            The new message
	 */
	public void setMessage(String message) {
		this.messageLabel.setText(message);
	}

	/**
	 * Binds the progress bar's progress to another observable value.
	 * 
	 * @param progress
	 *            The DoubleProperty to bind it to
	 */
	public void bindProgress(ReadOnlyDoubleProperty progress) {
		progressBar.progressProperty().bind(progress);
	}
}
