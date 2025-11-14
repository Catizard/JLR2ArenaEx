package io.github.catizard.jlr2arenaex.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Group {
	private Client host;
	private List<Client> guests = new ArrayList<>();
	private String serverAddress;
	private int port;

	/**
	 * Default constructor, creates a group with 1 host and 1 guest. Server port is 2222
	 */
	public Group(String serverAddress) {
		this(serverAddress, 2222, 2);
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
	public Group(String serverAddress, int port, int size) {
		if (size == 0) {
			throw new IllegalArgumentException("size must be greater than 0");
		}
		this.serverAddress = serverAddress;
		this.port = port;
		this.host = new Client(buildURI(), "host");
		synchronized (host) {
			try {
				host.wait();
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		// guests must be connected after host to make sure their identity is guest
		for (int i = 1; i < size; i++) {
			this.guests.add(new Client(buildURI(), "guest" + i));
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
