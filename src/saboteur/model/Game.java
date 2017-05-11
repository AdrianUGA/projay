package saboteur.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import saboteur.ai.TemporarAI;
import saboteur.model.Card.*;
import saboteur.tools.Loader;

public class Game {
	private Player currentPlayer;
	private int round;
	private int turn;
	private final long seed;

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
        this.seed = 123456789;
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
		Collections.shuffle(this.goldCardStack);
		this.history = new LinkedList<>();
		this.newRound();
		this.round = 0;
	}

	public void newRound(){
		this.round++;
		this.trash = new LinkedList<>();
		this.stack = this.deck.getOtherCards();
		Collections.shuffle(this.stack);
		this.board = new Board(this.deck.getStartPathCard(), this.deck.getGoalPathCards());
		this.setTeam();
		this.turn = 1;
	}

	public Player getCurrentPlayer(){
		return this.currentPlayer;
	}

	public void nextPlayer(){
		this.currentPlayer = this.playerList.removeFirst();
		this.playerList.addLast(this.currentPlayer);
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
	
	public long getSeed() {
		return seed;
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

	public void setTeam(){
		ArrayList<Team> team = new ArrayList<>();
		int nbPlayer = this.playerList.size();
		team.add(Team.DWARF);
		team.add(Team.DWARF);
		team.add(Team.DWARF);
		team.add(Team.SABOTEUR);
		if (nbPlayer > 3){
			team.add(Team.DWARF);
		}
		if (nbPlayer > 4){
			team.add(Team.SABOTEUR);
		}
		if (nbPlayer > 5){
			team.add(Team.DWARF);
		}
		if (nbPlayer > 6){
			team.add(Team.SABOTEUR);
		}
		if (nbPlayer > 7){
			team.add(Team.DWARF);
		}
		if (nbPlayer > 8){
			team.add(Team.DWARF);
		}
		if (nbPlayer > 9){
			team.add(Team.SABOTEUR);
		}
		Collections.shuffle(team);
		for(int i = 0; i < this.playerList.size(); i++){
			Team role = team.get(0);
			this.playerList.get(i).setTeam(role);
			if (this.playerList.get(i).isAI()){
				this.playerList.set(i, ((TemporarAI)this.playerList.get(i)).getNewAI(role));
			}
		}
	}
}
