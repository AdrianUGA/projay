package saboteur.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import saboteur.model.Card.*;

public class Board {
	private static final int GRID_SIZE = 61;
	private static final Position START = new Position(30,30);
	
	private PathCard[][] board;
	private Map<Position, Float> objectiveCards;
	
	public Board(){
		this.board = new PathCard[GRID_SIZE][GRID_SIZE];
		this.objectiveCards = new HashMap<Position, Float>();
	}
	
	public void addCard(PathCard card, Position position){
		if(card.getClass().getName() == "GoldCard")
			this.objectiveCards.put(position, 1f/3);

		this.board[position.getcY()][position.getcX()] = card;
	}
	
	public void removeCard(Position position){
		this.board[position.getcY()][position.getcX()] = null;
	}
	
	public PathCard getCard(Position position){
		return this.board[position.getcY()][position.getcX()];
	}
	
	public int getTaxiDistance(Position p1, Position p2) {
		return Math.abs(p2.getcY() - p1.getcY()) + Math.abs(p2.getcX() - p1.getcX());
	}
	
	public int numberOfNeighbors(Position position){
		return (this.board[position.getcY()+1][position.getcX()] != null ? 1 : 0)
				+(this.board[position.getcY()-1][position.getcX()] != null ? 1 : 0)
				+(this.board[position.getcY()][position.getcX()+1] != null ? 1 : 0)
				+(this.board[position.getcY()][position.getcX()-1] != null ? 1 : 0);
	}
	
	public Position getPositionCard(PathCard card){
		PathCard current;
		for (int i=0; i<GRID_SIZE; i++){
			for (int j=0; j<GRID_SIZE; j++){
				current = board[i][j];
				if (current != null && current == card){
					return new Position(j, i);
				}
			}
		}
		return new Position(-1, -1);
	}
	
	public Map<Position, Float> getGoldCards(){
		return this.objectiveCards;
	}
}
