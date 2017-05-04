package saboteur.model;

import saboteur.model.Card.*;

public class Board {
	private static final int GRID_SIZE = 89;
	
	private PathCard[][] board;
	
	public Board(){
		this.board = new PathCard[GRID_SIZE][GRID_SIZE];
	}
	
	public void addCard(PathCard card, Position position){
		this.board[position.getcX()][position.getcY()] = card;
	}
	
	public void removeCard(Position position){
		this.board[position.getcX()][position.getcY()] = null;
	}
	
	public int getTaxiDistance(Position p1, Position p2) {
		return Math.abs(p2.getcX() - p1.getcX()) + Math.abs(p2.getcY() - p1.getcY());
	}
	
}
