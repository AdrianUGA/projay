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
		int xStart = Board.getStart().getcX();
		int yStart = Board.getStart().getcY();
		String cross[] = {"NORTH", "EAST", "SOUTH", "WEST"};
		String horizontalBar[] = {"WEST", "EAST"};
		String verticalBar[] = {"NORTH", "SOUTH"};
		String angleRight[] = {"SOUTH", "EAST"};
		String angleLeft [] = {"SOUTH", "WEST"};
		PathCard crossCard = new PathCard(cross, false, false, false, false);
		PathCard horizontalBarCard = new PathCard(horizontalBar, false, false, false, false);
		PathCard verticalBarCard = new PathCard(verticalBar, false, false, false, false);
		PathCard angleRightCardSouth = new PathCard(angleRight, false, false, false, false);
		PathCard angleLeftCardSouth = new PathCard(angleLeft, false, false, false, false);
		PathCard angleLeftCardNorth = angleRightCardSouth.clone();
		angleLeftCardNorth.reverse();
		PathCard angleRigthCardNorth = angleLeftCardSouth.clone();
		angleRigthCardNorth.reverse();
		
		PathCard start = new PathCard(cross, false, true, false, false);
		PathCard goalWithGold = new PathCard(cross, false, false, true, true);
		goalWithGold.setVisible(false);
		PathCard goal = new PathCard(cross, false, false, true, false);
		goal.setVisible(false);
		ArrayList<PathCard> startCards = new ArrayList<>();
		startCards.add(start);
		ArrayList<PathCard> goalCards = new ArrayList<>();
		goalCards.add(goal);
		goalCards.add(goal);
		goalCards.add(goalWithGold);
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
		
		assertEquals(true, boardTest.isPossible(angleRightCardSouth, new Position(xStart-1,yStart-1)));
		assertEquals(true, boardTest.isPossible(angleLeftCardSouth, new Position(xStart+1,yStart-1)));
		assertEquals(true, boardTest.isPossible(angleLeftCardNorth, new Position(xStart+1,yStart+1)));
		assertEquals(true, boardTest.isPossible(angleRigthCardNorth, new Position(xStart-1,yStart+1)));
		boardTest.addCard(angleRightCardSouth, new Position(xStart-1,yStart-1));
		boardTest.addCard(angleLeftCardSouth, new Position(xStart+1,yStart-1));
		boardTest.addCard(angleLeftCardNorth, new Position(xStart+1,yStart+1));
		boardTest.addCard(angleRigthCardNorth, new Position(xStart-1,yStart+1));
		
		assertEquals(true, boardTest.isPossible(angleRightCardSouth, new Position(xStart,yStart-2)));
		assertEquals(true, boardTest.isPossible(angleLeftCardSouth, new Position(xStart+2,yStart)));
		assertEquals(true, boardTest.isPossible(angleLeftCardNorth, new Position(xStart,yStart+2)));
		assertEquals(true, boardTest.isPossible(angleRigthCardNorth, new Position(xStart-2,yStart)));
		boardTest.addCard(angleRightCardSouth, new Position(xStart,yStart-2));
		boardTest.addCard(angleLeftCardSouth, new Position(xStart+2,yStart));
		boardTest.addCard(angleLeftCardNorth, new Position(xStart,yStart+2));
		boardTest.addCard(angleRigthCardNorth, new Position(xStart-2,yStart));
		
		assertEquals(false, boardTest.isPossible(angleLeftCardSouth, new Position(xStart+1,yStart-2)));
		assertEquals(false, boardTest.isPossible(angleLeftCardNorth, new Position(xStart+2,yStart+1)));
		assertEquals(false, boardTest.isPossible(angleRigthCardNorth, new Position(xStart-1,yStart+2)));
		assertEquals(false, boardTest.isPossible(angleRightCardSouth, new Position(xStart-2,yStart-1)));
		
		assertEquals(true, boardTest.isPossible(horizontalBarCard, new Position(xStart+1,yStart-2)));
		assertEquals(true, boardTest.isPossible(verticalBarCard, new Position(xStart+2,yStart+1)));
		assertEquals(true, boardTest.isPossible(horizontalBarCard, new Position(xStart-1,yStart+2)));
		assertEquals(true, boardTest.isPossible(verticalBarCard, new Position(xStart-2,yStart-1)));
		
		assertEquals(false, boardTest.isPossible(horizontalBarCard, new Position(xStart+Board.DISTANCE_START_OBJECTIVE_X-1,yStart)));
		goal.setVisible(true);
		goalWithGold.setVisible(true);
		assertEquals(true, boardTest.isPossible(horizontalBarCard, new Position(xStart+Board.DISTANCE_START_OBJECTIVE_X-1,yStart)));
	}

}
