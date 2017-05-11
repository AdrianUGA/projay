package saboteur.model;

import java.util.Collections;
import java.util.LinkedList;

import saboteur.model.Card.*;
import saboteur.tools.Loader;

public class Game {
	private Player currentPlayer;
	private int round;
	private int turn;

	private final Deck deck;

	private LinkedList<GoldCard> goldCardStack;
	private LinkedList<Operation> history;

	private LinkedList<Card> stack;
	private LinkedList<Card> trash;

	private LinkedList<Player> playerList;

	private Board board;
	
	private LinkedList<Player> observers;

	public Game(){
        Loader loader = new Loader();
        deck = loader.loadCard();
        this.observers = new LinkedList<>();
        this.playerList = new LinkedList<>();
    }
	
	public void addPlayer(Player player){
		this.playerList.add(player);
	}
	
	public void playOperation(Operation op){
		this.history.add(op);
		op.exec(this);
	}

	public void newGame(){
		this.goldCardStack = this.deck.getGoldCards();
		this.history = new LinkedList<>();
	}

	public void newRound(){
		this.trash = new LinkedList<>();
		this.stack = this.deck.getOtherCards();
		Collections.shuffle(this.stack);
		this.board = new Board(this.deck.getStartPathCard(), this.deck.getGoalPathCards());
		//TODO définir le role de chaque joueur
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
	
	public boolean gameIsFinished(){
		if (round == 3 && roundIsFinished()) return true;
		return false;
	}
	
	public boolean roundIsFinished(){
		if (this.board.goalCardWithGoldIsVisible()) return true;
		return false;
	}

	public LinkedList<Player> getPlayerList() {
		return playerList;
	}

	public Board getBoard() {
		return board;
	}
	
	/* This method is needed by our fellow AI */
	public LinkedList<Operation> getHistory() {
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
	
	public int getTurn(){
		return turn;
	}
}
