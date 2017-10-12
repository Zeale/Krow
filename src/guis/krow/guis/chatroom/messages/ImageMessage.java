package krow.guis.chatroom.messages;

import javafx.scene.image.Image;
import kr�w.connections.Message;

public class ImageMessage extends Message {

	private final Image image;

	public ImageMessage(Image image) {
		this.image = image;
	}

	public Image getImage() {
		return image;
	}

}
