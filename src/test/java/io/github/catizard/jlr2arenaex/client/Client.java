package io.github.catizard.jlr2arenaex.client;

import io.github.catizard.jlr2arenaex.enums.ClientToServer;
import io.github.catizard.jlr2arenaex.enums.ServerToClient;
import io.github.catizard.jlr2arenaex.network.PackUtil;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class Client extends WebSocketClient {
	private String userName;
	private boolean connected;
	private List<Message> messages = new ArrayList<>();

	public Client(URI serverURI, String userName) {
		super(serverURI);
		this.userName = userName;
		this.setConnectionLostTimeout(0);
		this.connect();
	}

	@Override
	public void onOpen(ServerHandshake serverHandshake) {
		send(ClientToServer.CTS_USERNAME, this.userName.getBytes());
	}

	@Override
	public void onMessage(String s) {
	}

	@Override
	public void onMessage(ByteBuffer bytes) {
		try {
			parsePacket(bytes);
		} catch (IOException e) {
			e.printStackTrace();
			// submitError(e);
		}
	}

	@Override
	public void onClose(int i, String s, boolean b) {

	}

	@Override
	public void onError(Exception e) {
		// submitError(e);
	}

	public void send(ClientToServer id, byte[] data) {
		if (this.isOpen()) {
			super.send(PackUtil.concat((byte) id.getValue(), data));
		}
	}

	public List<Message> getMessages() {
		return new ArrayList<>(this.messages);
	}

	public boolean isConnected() {
		return connected;
	}

	/**
	 * Compare two clients' messages
	 * @param client other client
	 */
	public boolean compareMessages(Client client) {
		if (this.messages.size() != client.messages.size()) {
			return false;
		}
	}

	private void parsePacket(ByteBuffer bytes) throws IOException {
		char id = ((char) bytes.get());
		ServerToClient ev = ServerToClient.from(id);
		byte[] data = new byte[bytes.remaining()];
		bytes.get(data, 0, data.length);
		messages.add(new Message(ev, data));
		if (ev == ServerToClient.STC_CLIENT_REMOTE_ID) {
			synchronized (this) {
				this.notify();
			}
		}
	}
}
