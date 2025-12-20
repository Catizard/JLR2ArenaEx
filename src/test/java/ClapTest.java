import io.github.catizard.jlr2arenaex.network.Pair;
import io.github.catizard.jlr2arenaex.network.Score;
import io.github.catizard.jlr2arenaex.network.SelectedBMSMessage;
import io.github.catizard.jlr2arenaex.server.ArenaServer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled("This test needs a running LR2ArenaEx server instance")
public class ClapTest {
	@Test
	public void smokeTest() throws InterruptedException {
		var _p = startServer();

		Group group1 = new Group("group1", "127.0.0.1", 2222, 2);
		Group group2 = new Group("group2", "127.0.0.1", 2223, 2);

		Thread.sleep(500);

		_p.getFirst().stop();
		_p.getSecond().interrupt();
		assertEquals(group1, group2);
	}

	/**
	 * This test goes through a basic routine:
	 * <ol>
	 *     <li>Create a lobby, which has two players: host and guest</li>
	 *     <li>Host picks a song</li>
	 *     <li>Guest get ready</li>
	 *     <li>Host send a several score messages</li>
	 *     <li>Guest send a several score messages</li>
	 *     <li>Host picks another song</li>
	 *     <li>Routine ends</li>
	 * </ol>
	 * NOTE: In order to test this easily, we won't let host and guest send the score messages parallelly
	 */
	@Test
	public void basicRoutineTest() throws InterruptedException {
		var _p = startServer();

		Group group1 = new Group("upstream", "127.0.0.1", 2222, 2);
		Group group2 = new Group("fork", "127.0.0.1", 2223, 2);

		group1.pickSongByHost(mockSelectedBMSMessage1());
		group2.pickSongByHost(mockSelectedBMSMessage1());

		Thread.sleep(200);

		group1.pickSongByGuest(mockSelectedBMSMessage1());
		group2.pickSongByGuest(mockSelectedBMSMessage1());

		Score hostScore = new Score();
		for (int i = 0; i < 5;++i) {
			randomPlusOne(hostScore);
			Score p = hostScore.clone();
			group1.updateScoreByHost(p);
			group2.updateScoreByHost(p);
		}

		Thread.sleep(200);

		Score guestScore = new Score();
		for (int i = 0; i < 5;++i) {
			randomPlusOne(guestScore);
			Score p = guestScore.clone();
			group1.updateScoreByGuest(p);
			group2.updateScoreByGuest(p);
		}

		group1.pickSongByHost(mockSelectedBMSMessage2());
		group2.pickSongByHost(mockSelectedBMSMessage2());

		Thread.sleep(5000);

		_p.getFirst().stop();
		_p.getSecond().interrupt();
		assertEquals(group1, group2);
	}

	private Pair<ArenaServer, Thread> startServer() {
		ArenaServer server = new ArenaServer(2223);
		Thread runner = new Thread(server);
		runner.start();
		return Pair.of(server, runner);
	}

	private SelectedBMSMessage mockSelectedBMSMessage1() {
		return new SelectedBMSMessage(
				11739,
				"8e054517fc9c386cf03dbed444f67171",
				"-Never ending journey-[BEGINNER]",
				"SOMON",
				0,
				0,
				false
		);
	}

	private SelectedBMSMessage mockSelectedBMSMessage2() {
		return new SelectedBMSMessage(
				11739,
				"c791696cbb30375e37e184e763833f27",
				"菖蒲日和",
				"Y.W / BGA&obj: kotomi",
				0,
				0,
				false
		);
	}

	private void randomPlusOne(Score score) {
		Random rand = new Random();
		int v = ((rand.nextInt() % 5) + 5) % 5;
		switch (v) {
			case 0 -> score.setPoor(score.getPoor() + 1);
			case 1 -> score.setBad(score.getBad() + 1);
			case 2 -> {
				score.setGood(score.getGood() + 1);
				score.setMaxCombo(score.getMaxCombo() + 1);
			}
			case 3 -> {
				score.setGreat(score.getGreat() + 1);
				score.setMaxCombo(score.getMaxCombo() + 1);
				score.setScore(score.getScore() + 1);
			}
			case 4 -> {
				score.setpGreat(score.getpGreat() + 1);
				score.setMaxCombo(score.getMaxCombo() + 1);
				score.setScore(score.getScore() + 2);
			}
		}
		score.setCurrentNotes(score.getCurrentNotes() + 1);
	}

}
