package saboteur.model;

import java.util.LinkedList;
import java.util.List;

import saboteur.model.Card.*;

public class Board {
	private static final int GRID_SIZE = 61;
	private static final Position START = new Position(30,30);
	private static final int DISTANCE_START_OBJECTIVE_X = 7;
	private static final int DISTANCE_START_OBJECTIVE_Y = 2;
	
	private PathCard[][] board;
	private List<Position> objectiveCards;
	
	public Board(){
		this.board = new PathCard[GRID_SIZE][GRID_SIZE];
		this.objectiveCards = new LinkedList<Position>();
		objectiveCards.add(new Position(START.getcX() + DISTANCE_START_OBJECTIVE_X, START.getcY()));
		objectiveCards.add(new Position(START.getcX() + DISTANCE_START_OBJECTIVE_X, START.getcY() + DISTANCE_START_OBJECTIVE_Y));
		objectiveCards.add(new Position(START.getcX() + DISTANCE_START_OBJECTIVE_X, START.getcY() - DISTANCE_START_OBJECTIVE_Y));
	}
	
	public void addCard(PathCard card, Position position){
		if(card.getClass().getName() == "GoldCard")
			this.objectiveCards.add(position);

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
	
	public List<Position> getGoldCards(){
		return this.objectiveCards;
	}
	
	public boolean isPossible(PathCard card, Position position){
		
		return true;
	}
}
