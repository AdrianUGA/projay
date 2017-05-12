package modelTest;

import static org.junit.Assert.*;

import org.junit.Test;

import saboteur.model.Position;
import saboteur.model.Card.Cardinal;

public class PositionTest {
	
	@Test
	public void testPosition() {
		fail("Not yet implemented");
	}

	@Test
	public void testGet() {
		Position p = new Position(10, 12);
		assertEquals(p.getcX(), 10);
		assertEquals(p.getcY(), 12);
	}


	@Test
	public void testGreater() {
		Position p = new Position(10, 20);
		Position p2 = new Position(15, 15);
		assertEquals(p.greaterX(p2), false);
		assertEquals(p.greaterY(p2), true);
		assertEquals(p2.greaterX(p), true);
		assertEquals(p2.greaterY(p), false);
	}

	@Test
	public void testGetNeighbor() {
		Position p1 = new Position(10, 20);
		assertEquals(p1.getNeighbor(Cardinal.EAST).getcX(), 10);
		assertNull((new Position(0, 0)).getNeighbor(Cardinal.NORTH));
	}

	@Test
	public void testIsValid() {
		assertTrue((new Position(10,20)).isValid());
		assertFalse((new Position(-1, 20)).isValid());
	}

	@Test
	public void testEqualsObject() {
		assertTrue((new Position(10,20)).equals(new Position(10, 20)));
		assertFalse((new Position(10,20)).equals(new Position(20, 10)));
	}

}
