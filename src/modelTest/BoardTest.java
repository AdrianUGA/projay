package modelTest;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import saboteur.model.Board;
import saboteur.model.*;
import saboteur.model.Card.*;

public class BoardTest {

	//NOT SUCCESS CURRENTLY : Due to board's constructor
	@Test
	public void testIsPossible() {
		int xStart = Board.START.getcX();
		int yStart = Board.START.getcY();
		String cross[] = {"NORTH", "EAST", "SOUTH", "WEST"};
		String horizontalBar[] = {"WEST", "EAST"};
		String verticalBar[] = {"NORTH", "SOUTH"};
		String angleRight[] = {"SOUTH", "EAST"};
		String angleLeft [] = {"SOUTH", "WEST"};
		PathCard crossCard = new PathCard(cross, false, false, false, false);
		PathCard horizontalBarCard = new PathCard(horizontalBar, false, false, false, false);
		PathCard verticalBarCard = new PathCard(verticalBar, false, false, false, false);
		PathCard angleRightCard = new PathCard(angleRight, false, false, false, false);
		PathCard angleLeftCard = new PathCard(angleLeft, false, false, false, false);
		
		PathCard start = new PathCard(cross, false, true, false, false);
		PathCard goalWithGold = new PathCard(cross, false, false, true, true);
		goalWithGold.setVisible(false);
		PathCard goal = new PathCard(cross, false, false, true, false);
		goal.setVisible(false);
		ArrayList<PathCard> startCards = new ArrayList<>();
		startCards.add(start);
		ArrayList<PathCard> goalCards = new ArrayList<>();
		startCards.add(goal);
		startCards.add(goal);
		startCards.add(goalWithGold);
		Board boardTest = new Board(startCards, goalCards);
		
		assertEquals(true, boardTest.isPossible(crossCard, new Position(xStart,yStart-1)));
		assertEquals(true, boardTest.isPossible(crossCard, new Position(xStart+1,yStart)));
		assertEquals(true, boardTest.isPossible(crossCard, new Position(xStart,yStart+1)));
		assertEquals(true, boardTest.isPossible(crossCard, new Position(xStart-1,yStart)));
		boardTest.addCard(crossCard, new Position(xStart,yStart-1));
		boardTest.addCard(crossCard, new Position(xStart+1,yStart));
		boardTest.addCard(crossCard, new Position(xStart,yStart+1));
		boardTest.addCard(crossCard, new Position(xStart-1,yStart));
		
		//test add card without neighbor
		assertEquals(false, boardTest.isPossible(crossCard, new Position(xStart-10,yStart-10)));
		
		assertEquals(false, boardTest.isPossible(verticalBarCard, new Position(xStart-1,yStart-1)));
		assertEquals(false, boardTest.isPossible(horizontalBarCard, new Position(xStart+1,yStart-1)));
		assertEquals(false, boardTest.isPossible(verticalBarCard, new Position(xStart+1,yStart+1)));
		assertEquals(false, boardTest.isPossible(horizontalBarCard, new Position(xStart-1,yStart+1)));
		
		assertEquals(true, boardTest.isPossible(angleRightCard, new Position(xStart-1,yStart-1)));
		assertEquals(true, boardTest.isPossible(angleLeftCard, new Position(xStart+1,yStart-1)));
		assertEquals(true, boardTest.isPossible(angleRightCard.reversed(), new Position(xStart+1,yStart+1)));
		assertEquals(true, boardTest.isPossible(angleLeftCard.reversed(), new Position(xStart-1,yStart+1)));
		boardTest.addCard(angleRightCard, new Position(xStart-1,yStart-1));
		boardTest.addCard(angleLeftCard, new Position(xStart+1,yStart-1));
		boardTest.addCard(angleRightCard.reversed(), new Position(xStart+1,yStart+1));
		boardTest.addCard(angleLeftCard.reversed(), new Position(xStart-1,yStart+1));
		
		assertEquals(true, boardTest.isPossible(angleRightCard, new Position(xStart,yStart-2)));
		assertEquals(true, boardTest.isPossible(angleLeftCard, new Position(xStart+2,yStart)));
		assertEquals(true, boardTest.isPossible(angleRightCard.reversed(), new Position(xStart,yStart+2)));
		assertEquals(true, boardTest.isPossible(angleLeftCard.reversed(), new Position(xStart-2,yStart)));
		boardTest.addCard(angleRightCard, new Position(xStart,yStart-2));
		boardTest.addCard(angleLeftCard, new Position(xStart+2,yStart));
		boardTest.addCard(angleRightCard.reversed(), new Position(xStart,yStart+2));
		boardTest.addCard(angleLeftCard.reversed(), new Position(xStart-2,yStart));
		
		assertEquals(false, boardTest.isPossible(angleLeftCard, new Position(xStart+1,yStart-2)));
		assertEquals(false, boardTest.isPossible(angleRightCard.reversed(), new Position(xStart+2,yStart+1)));
		assertEquals(false, boardTest.isPossible(angleLeftCard.reversed(), new Position(xStart-1,yStart+2)));
		assertEquals(false, boardTest.isPossible(angleRightCard, new Position(xStart-2,yStart-1)));
		
		assertEquals(true, boardTest.isPossible(horizontalBarCard, new Position(xStart+1,yStart-2)));
		assertEquals(true, boardTest.isPossible(verticalBarCard, new Position(xStart+2,yStart+1)));
		assertEquals(true, boardTest.isPossible(horizontalBarCard, new Position(xStart-1,yStart+2)));
		assertEquals(true, boardTest.isPossible(verticalBarCard, new Position(xStart-2,yStart-1)));
		
		assertEquals(false, boardTest.isPossible(horizontalBarCard, new Position(xStart+Board.DISTANCE_START_OBJECTIVE_X-1,yStart)));
		goal.setVisible(true);
		assertEquals(true, boardTest.isPossible(horizontalBarCard, new Position(xStart+Board.DISTANCE_START_OBJECTIVE_X-1,yStart)));
	}

}
