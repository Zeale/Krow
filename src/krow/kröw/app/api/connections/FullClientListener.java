package kr�w.app.api.connections;

public interface FullClientListener extends ClientListener {

	void connectionEstablished();

	void connectionLost();

	void connectionClosed();

}
