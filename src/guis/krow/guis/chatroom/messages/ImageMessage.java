package krow.guis.chatroom.messages;

import javafx.scene.image.Image;
import kröw.connections.messages.Message;

public class ImageMessage extends Message {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;
	private final Image image;

	public ImageMessage(final Image image) {
		// TODO save image as serializable format
		this.image = image;
	}

	public Image getImage() {
		return image;
	}

}
