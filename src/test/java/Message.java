import io.github.catizard.jlr2arenaex.enums.ServerToClient;
import io.github.catizard.jlr2arenaex.network.EqualsWithoutRandomPort;

import java.util.Arrays;

public class Message<T extends EqualsWithoutRandomPort<T>> implements EqualsWithoutRandomPort<Message<T>> {
	private ServerToClient id;
	private T data;
	private byte[] rawData;

	public Message(ServerToClient id, T data, byte[] rawData) {
		this.id = id;
		this.data = data;
		this.rawData = rawData;
	}

	public ServerToClient getId() {
		return id;
	}

	public T getData() {
		return data;
	}

	public byte[] getRawData() {
		return rawData;
	}

	@Override
	public boolean equalsWithoutRandomPort(Message<T> obj) {
		return id == obj.id && data.equalsWithoutRandomPort(obj.data);
	}

	@Override
	public String toString() {
		return String.format("%s(%d) %s", id.name(), id.ordinal(), Arrays.toString(rawData));
	}
}
