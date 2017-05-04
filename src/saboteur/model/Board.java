package saboteur.model;

import saboteur.model.Card.*;

public class Board {
	private PathCard[][] board;
	
	public Board(){
		this.board = new PathCard[89][89];
	}
	
	public void addCard(PathCard card, Position position){
		this.board[position.getcX()][position.getcY()] = card;
	}
	
	public void removeCard(Position position){
		this.board[position.getcX()][position.getcY()] = null;
	}
	
	public int getTaxiDistance(Position p1, Position p2) {
		return Math.abs(p2.getcX() - p1.getcX() + p2.getcY() - p1.getcY());
	}
	
}
