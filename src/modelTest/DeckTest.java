package modelTest;

import org.junit.Test;
import saboteur.model.Card.Deck;
import saboteur.tools.Loader;

import static org.junit.Assert.assertEquals;

public class DeckTest {

	@Test
	public void testGetOtherCards() {
		Loader loader = new Loader();
		Deck deck = loader.loadCard();
		assertEquals(true, deck.getOtherCards().size() == deck.getCopyOtherCards().size());

		for(int i = 0; i < deck.getOtherCards().size(); i++){
			assertEquals(false, deck.getOtherCards().get(i) == deck.getCopyOtherCards().get(i));
		}
	}

	@Test
	public void testGetGoldCards() {
		Loader loader = new Loader();
		Deck deck = loader.loadCard();
		assertEquals(true, deck.getGoldCards().size() == deck.getCopyGoldCards().size());

		for(int i = 0; i < deck.getGoldCards().size(); i++){
			assertEquals(false, deck.getGoldCards().get(i) == deck.getCopyGoldCards().get(i));
		}
	}

	@Test
	public void testGetGoalPathCards() {
		Loader loader = new Loader();
		Deck deck = loader.loadCard();
		assertEquals(true, deck.getGoalPathCards().size() == deck.getCopyGoalPathCards().size());

		for(int i = 0; i < deck.getGoalPathCards().size(); i++){
			assertEquals(false, deck.getGoalPathCards().get(i) == deck.getCopyGoalPathCards().get(i));
		}
	}

	@Test
	public void testGetStartPathCard() {
		Loader loader = new Loader();
		Deck deck = loader.loadCard();
		assertEquals(true, deck.getStartPathCard().size() == deck.getCopyStartPathCard().size());
		for(int i = 0; i < deck.getStartPathCard().size(); i++){
			assertEquals(false, deck.getStartPathCard().get(i) == deck.getCopyStartPathCard().get(i));
		}
	}

}
