package io.github.catizard.jlr2arenaex;

import io.github.catizard.jlr2arenaex.client.Group;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClapTest {
	@Test
	public void smokeTest() {
		Group group1 = new Group("127.0.0.1");
		Group group2 = new Group("127.0.0.1");

		assertEquals(group1, group2);
	}
}

