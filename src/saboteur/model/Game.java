package saboteur.model;

import java.util.ArrayList;

import saboteur.model.Card.*;

public class Game {
	private Player currentPlayer;
	private int round;
	private ArrayList<GoldCard> goldCardStack;
	private ArrayList<Operation> history;


	private ArrayList<Card> stack;
	private ArrayList<Card> trash;
	private ArrayList<Player> playerList;
	private Board board;
	
	public void addPlayer(Player player){
		this.playerList.add(player);
	}
	
	public void playOperation(Operation op){
		//TODO
	}
	
	public void save(){
		//TODO
	}
	
	public void load(){
		//TODO
	}
	
	public void draw(){
		//TODO
	}
	
	public boolean isFinish(){
		//TODO
		return false;
	}

	public Board getBoard() {
		return board;
	}
	
	/* This method is needed by our fellow AI */
	public ArrayList<Operation> getHistory() {
		return this.history;
	}
}
