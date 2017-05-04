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
	
}
