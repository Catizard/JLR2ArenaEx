import io.github.catizard.jlr2arenaex.enums.ClientToServer;
import io.github.catizard.jlr2arenaex.enums.ServerToClient;
import io.github.catizard.jlr2arenaex.network.Address;
import io.github.catizard.jlr2arenaex.network.PackUtil;
import io.github.catizard.jlr2arenaex.network.PeerList;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Client extends WebSocketClient {
	private static final Logger logger = LoggerFactory.getLogger(Client.class);

	private String groupName;
	private String userName;
	private boolean connected;
	private List<Message<?>> messages = new ArrayList<>();

	public Client(String groupName, URI serverURI, String userName) {
		super(serverURI);
		this.groupName = groupName;
		this.userName = userName;
		this.setConnectionLostTimeout(1);
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

	public List<Message<?>> getMessages() {
		return new ArrayList<>(this.messages);
	}

	public boolean isConnected() {
		return connected;
	}

	public String getFullName() {
		return String.format("%s(%s)", this.userName, this.groupName);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Client client = (Client) o;
		return compareMessages(client);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userName, connected, messages);
	}

	/**
	 * Compare two clients' messages
	 *
	 * @param client other client
	 */
	public boolean compareMessages(Client client) {
		boolean matched = true;
		int minSize = Math.min(this.messages.size(), client.messages.size());
		for (int i = 0; i < minSize; ++i) {
			Message selfMessage = this.messages.get(i);
			Message rhsMessage = client.messages.get(i);
			if (selfMessage.equalsWithoutRandomPort(rhsMessage)) {
				logger.trace("[{}] message matched: {}", i, this.messages.get(i));
			} else {
				matched = false;
				logger.error("[{}-{}<>{}] message mismatched: \n- {}\n- {}",
						i, this.getFullName(), client.getFullName(),
						this.messages.get(i), selfMessage);
			}
		}
		List<Message<?>> remainingMessages = this.messages.size() > minSize
				? this.messages.subList(minSize, this.messages.size())
				: client.messages.subList(minSize, client.messages.size());
		for (int i = 0; i < remainingMessages.size(); ++i) {
			matched = false;
			logger.error("[{}-{}] extra message received: {}",
					i + minSize, this.messages.size() > minSize ? this.getFullName() : client.getFullName(),
					remainingMessages.get(i));
		}
		return matched;
	}

	private void parsePacket(ByteBuffer bytes) throws IOException {
		char id = ((char) bytes.get());
		ServerToClient ev = ServerToClient.from(id);
		byte[] data = new byte[bytes.remaining()];
		bytes.get(data, 0, data.length);
		Value value;
		try (MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(data)) {
			value = unpacker.unpackValue();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		switch (ev) {
			case STC_CLIENT_REMOTE_ID -> {
				synchronized (this) {
					this.notifyAll();
				}
				messages.add(new Message<>(ev, new Address(value), data));
			}
			case STC_USERLIST -> {
				messages.add(new Message<>(ev, new PeerList(value), data));
			}
			default -> {
				logger.warn("[{}] is ignoring a packet: {}({}), data: {}", this.getFullName(), ev.name(), ev.ordinal(), Arrays.toString(data));
			}
		}
	}
}