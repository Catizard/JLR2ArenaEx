import io.github.catizard.jlr2arenaex.server.ArenaServer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("This test needs a running LR2ArenaEx server instance")
public class ClapTest {
	private Thread startServer() {
		ArenaServer server = new ArenaServer(2223);
		Thread runner = new Thread(server);
		runner.start();
		return runner;
	}

	@Test
	public void smokeTest() {
		Thread t = startServer();

		Group group1 = new Group("group1", "127.0.0.1", 2222, 2);
		Group group2 = new Group("group2", "127.0.0.1", 2223, 2);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		t.interrupt();
		assertEquals(group1, group2);
	}
}
