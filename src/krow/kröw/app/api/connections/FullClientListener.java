package kröw.app.api.connections;

public interface FullClientListener extends ClientListener {

	void connectionEstablished();

	void connectionLost();

	void connectionClosed();

}
