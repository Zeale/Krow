package kr�w.connections;

public interface FullClientListener extends ClientListener {

	void connectionClosed();

	void connectionEstablished();

	void connectionLost();

}
