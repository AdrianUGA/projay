package saboteur.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import saboteur.model.Card.*;

public class Board {
	private static final int GRID_SIZE = 89;
	
	private PathCard[][] board;
	private Map<Position, Float> goldCards;
	
	public Board(){
		this.board = new PathCard[GRID_SIZE][GRID_SIZE];
		this.goldCards = new HashMap<Position, Float>();
	}
	
	public void addCard(PathCard card, Position position){
		if(card.getClass().getName() == "GoldCard")
			this.goldCards.put(position, 1f/3);
		//TODO WARNING : Position x of a position correspond to second index of a table
		this.board[position.getcX()][position.getcY()] = card;
	}
	
	public void removeCard(Position position){
		//TODO WARNING : Position x of a position correspond to second index of a table
		this.board[position.getcX()][position.getcY()] = null;
	}
	
	public PathCard getCard(Position position){
		return this.board[position.getcY()][position.getcX()];
	}
	
	public int getTaxiDistance(Position p1, Position p2) {
		//TODO WARNING : Position x of a position correspond to second index of a table
		return Math.abs(p2.getcX() - p1.getcX()) + Math.abs(p2.getcY() - p1.getcY());
	}
	
	public int numberOfNeighbors(Position position){
		//TODO WARNING : Position x of a position correspond to second index of a table
		return (this.board[position.getcX()+1][position.getcY()] != null ? 1 : 0)
				+(this.board[position.getcX()-1][position.getcY()] != null ? 1 : 0)
				+(this.board[position.getcX()][position.getcY()+1] != null ? 1 : 0)
				+(this.board[position.getcX()][position.getcY()-1] != null ? 1 : 0);
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
		return this.goldCards;
	}
}
