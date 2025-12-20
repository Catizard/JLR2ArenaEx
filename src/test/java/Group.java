import io.github.catizard.jlr2arenaex.enums.ClientToServer;
import io.github.catizard.jlr2arenaex.network.Score;
import io.github.catizard.jlr2arenaex.network.ScoreMessage;
import io.github.catizard.jlr2arenaex.network.SelectedBMSMessage;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Group {
	private String name;
	private Client host;
	private List<Client> guests = new ArrayList<>();
	private String serverAddress;
	private int port;

	/**
	 * Default constructor, creates a group with 1 host and 1 guest. Server port is 2222
	 */
	public Group(String name, String serverAddress) {
		this(name, serverAddress, 2222, 2);
	}

	/**
	 * Initialize a group of clients. Group size is determined by parameter size.
	 * <ol>
	 *     <li>when size <= 0, throws exception immediately</li>
	 *     <li>when size == 1, creating a group with only host but no guest</li>
	 *     <li>when size > 1, creating a group with one host and size - 1 guests.
	 * </ol>
	 *
	 * @param serverAddress server ip address
	 * @param size cannot be zero
	 */
	public Group(String name, String serverAddress, int port, int size) {
		if (size == 0) {
			throw new IllegalArgumentException("size must be greater than 0");
		}
		this.name = name;
		this.serverAddress = serverAddress;
		this.port = port;
		this.host = new Client(name, buildURI(), "host");
		((new Thread( () -> host.connect(), String.format("Group(%s)-host", this.name)))).start();
		synchronized (host) {
			try {
				host.wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		// guests must be connected after host to make sure their identity is guest
		for (int i = 1; i < size; i++) {
			Client guest = new Client(name, buildURI(), "guest" + i);
			((new Thread(guest::connect, String.format("Group(%s)-guest-%d", this.name, i)))).start();
			synchronized (guest) {
				try {
					guest.wait();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
			this.guests.add(guest);
		}
	}

	public void pickSongByHost(SelectedBMSMessage selectedBMSMessage) {
		host.send(ClientToServer.CTS_SELECTED_BMS, selectedBMSMessage.pack());
		host.send(ClientToServer.CTS_LOADING_COMPLETE, "".getBytes());
	}

	public void pickSongByGuest(SelectedBMSMessage selectedBMSMessage) {
		for (Client guest : guests) {
			guest.send(ClientToServer.CTS_SELECTED_BMS, selectedBMSMessage.pack());
			guest.send(ClientToServer.CTS_LOADING_COMPLETE, "".getBytes());
		}
	}

	public void updateScoreByHost(Score score) {
		host.send(ClientToServer.CTS_PLAYER_SCORE, score.pack());
	}

	public void updateScoreByGuest(Score score) {
		for (Client guest : guests) {
			guest.send(ClientToServer.CTS_PLAYER_SCORE, score.pack());
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Group)) {
			return false;
		}
		Group rhs = (Group) obj;
		if (!this.host.equals(rhs.host)) {
			return false;
		}
		if (this.guests.size() > 1) {
			for (int i = 1; i < this.guests.size(); i++) {
				if (!this.guests.get(0).equals(this.guests.get(i))) {
					return false;
				}
			}
			if (!this.guests.get(0).equals(rhs.guests.get(0))) {
				return false;
			}
		}
		return true;
	}

	private URI buildURI() {
		try {
			return new URI(String.format("ws://%s:%d", serverAddress, port));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}