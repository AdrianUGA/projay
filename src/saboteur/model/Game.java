package saboteur.model;

import java.util.ArrayList;
import java.util.LinkedList;

import saboteur.model.Card.*;

public class Game {
	private Player currentPlayer;
	private int round;
	private ArrayList<GoldCard> goldCardStack;
	private ArrayList<Operation> history;

	private ArrayList<Card> stack;
	private ArrayList<Card> trash;
	private ArrayList<Player> playerList;
	public ArrayList<Player> getPlayerList() {
		return playerList;
	}

	private Board board;
	
	private LinkedList<Player> observers;
	
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

	public void register(Player player) {
		this.observers.add(player);
	}
	
	public void notify(Operation operation){
		for(Player player: this.observers){
			player.notify(operation);
		}
	}
}
