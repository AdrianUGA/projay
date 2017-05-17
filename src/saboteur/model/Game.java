package saboteur.model;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import javafx.scene.input.KeyCombination;
import saboteur.model.Card.*;
import saboteur.tools.Loader;

public class Game {

	private int currentPlayerIndex; //TO SAVE
	private int round; //TO SAVE
	private int turn; //TO SAVE
	public final static long seed = 223456789;

	private final Deck deck;//NOT TO SAVE

	private LinkedList<GoldCard> goldCardStack;//TO SAVE
	private LinkedList<Operation> history;//TO SAVE

	private LinkedList<Card> stack;//TO SAVE
	private LinkedList<Card> trash;//TO SAVE

	private LinkedList<Player> playerList;//TO SAVE

	private Board board;//TO SAVE
	
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
		this.round = 0;

		this.goldCardStack = this.deck.getCopyGoldCards();
		Collections.shuffle(this.goldCardStack, new Random(Game.seed));

		this.history = new LinkedList<>();

		this.currentPlayerIndex = this.playerList.size()-1;
		this.newRound();
	}

	public void newRound(){
		this.round++;
		this.turn = 1;

		this.trash = new LinkedList<>();
		this.stack = this.deck.getCopyOtherCards();
		Collections.shuffle(this.stack, new Random(Game.seed));

		this.board = new Board(this.deck.getCopyStartPathCard(), this.deck.getCopyGoalPathCards());

		this.setTeam();
		System.out.println("Round = " +this.round +" taille stack = "+ this.stack.size());
		this.dealCardsToPlayer();

		this.nextPlayer();
	}

	private void dealCardsToPlayer(){
		int nbPlayer = this.playerList.size();
		int nbCards;
		if (nbPlayer <= 5){
			nbCards = 6;
		} else if(nbPlayer <= 7){
			nbCards = 5;
		} else{
			nbCards = 4;
		}
		for (Player player: this.playerList) {
			ArrayList<Card> hand = new ArrayList<>();
			if(!this.stackIsEmpty()){
				for (int i = 0; i < nbCards; i++){
					hand.add(this.stack.removeFirst());
				}
			}
			player.setHand(hand);
		}
	}

	public Player getCurrentPlayer(){
		return this.playerList.get(this.currentPlayerIndex);
	}

	public void nextPlayer(){
		this.currentPlayerIndex = (this.currentPlayerIndex + 1) % this.playerList.size();
	}
	
	public void save(String name) {
		File dirSave = new File(Loader.savedFolder);
		dirSave.mkdir();
		File saveFile = new File(Loader.savedFolder+ "/" + name + ".save");
		try {
			if (saveFile.exists()){
				saveFile.delete();
			}
			saveFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
        try {
            FileOutputStream fileOutput = new FileOutputStream(saveFile);
            ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
            objectOutput.writeObject(this.currentPlayerIndex);
            objectOutput.writeObject(this.round);
            objectOutput.writeObject(this.turn);
            objectOutput.writeObject(this.goldCardStack);
            objectOutput.writeObject(this.history);
            objectOutput.writeObject(this.stack);
            objectOutput.writeObject(this.trash);
            objectOutput.writeObject(this.playerList);
            objectOutput.writeObject(this.board);
            objectOutput.writeObject(this.observers);
            objectOutput.close();
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void load(String name){
		File saveFile = new File(Loader.savedFolder+ "/" + name + ".save");
		FileInputStream fileInput;
		try {
			fileInput = new FileInputStream(saveFile);
	        ObjectInputStream objectInputStream = new ObjectInputStream(fileInput);
	        this.currentPlayerIndex = (int) objectInputStream.readObject();	        
	        this.round = (int) objectInputStream.readObject();
	        this.turn = (int) objectInputStream.readObject();
	        this.goldCardStack = (LinkedList<GoldCard>) objectInputStream.readObject();
            this.history = (LinkedList<Operation>) objectInputStream.readObject();
            this.stack = (LinkedList<Card>) objectInputStream.readObject();
            this.trash = (LinkedList<Card>) objectInputStream.readObject();
            this.playerList = (LinkedList<Player>) objectInputStream.readObject();
            this.board = (Board) objectInputStream.readObject();
            this.observers = (LinkedList<Player>) objectInputStream.readObject();
	        objectInputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void draw(){
		//TODO
	}
	
	public boolean gameIsFinished(){
		return round == 3 && roundIsFinished();
	}
	
	public boolean roundIsFinished(){
		return this.board.goalCardWithGoldIsVisible() || emptyHandsPlayers();
	}

	private boolean emptyHandsPlayers() {
		for (Player player : playerList){
			if (!player.emptyHand()) return false;
		}
		return true;
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
	
	public Card pick(){
		return this.stack.removeFirst();
	}
	
	public boolean stackIsEmpty(){
		return this.stack.isEmpty();
	}
	
	public void addCardToStack(Card card){
		this.stack.addFirst(card);
	}
	
	public boolean dwarfsWon(){
		return this.board.goalCardWithGoldIsVisible();
	}
	
	public LinkedList<Player> getPlayers(ActionCardToPlayer card){
		LinkedList<Player> result = new LinkedList<>();
		boolean isPossible = false;
		for (Player p : this.playerList){
			switch (card.getType()) {
				case SABOTAGE:
					isPossible = p.canHandicap((SabotageCard)card);
					break;
				case RESCUE:
					isPossible = p.canRescue((RescueCard)card);
					break;
				case DOUBLE_RESCUE:
					isPossible = p.canRescueWithDoubleRescueCard((DoubleRescueCard)card);
					break;
				default:
					break;
			}
			if (isPossible) result.add(p);
		}
		
		return result;
	}
	
	public void dealGold(){
		if (dwarfsWon()){
			Player current;
			GoldCard goldCard;
			int currentNumber = this.currentPlayerIndex;
			int nbCardsDealt = 0;
			while (nbCardsDealt <= (playerList.size()%9)){
				current = playerList.get(currentNumber);
				if (current.getTeam() == Team.DWARF){
					goldCard = goldCardStack.removeFirst();
					current.addGold(goldCard);
					nbCardsDealt++;
				}
				currentNumber = (currentNumber+1)%playerList.size();
			}
		} else {
			int nbSaboteurs = 0;
			int valueToDeal = 0;
			Player current;

			for (int i=0; i<this.playerList.size(); i++){
				if (this.playerList.get(i).getTeam() == Team.SABOTEUR) nbSaboteurs++;
			}
			
			switch (nbSaboteurs){
				case 0:
					break;
				case 1:
					valueToDeal = 4;
					break;
				case 2:
				case 3:
					valueToDeal = 3;
					break;
				case 4:
					valueToDeal = 2;
					break;
				default:
					System.err.println("Impossible to be here");
			}
			
			for (int i=0; i<this.playerList.size(); i++){
				current = this.playerList.get(i);
				if (current.getTeam() == Team.SABOTEUR){
					for (GoldCard card : getCardsToValue(valueToDeal)){
						current.addGold(card);
					}
				}
			}
		}
	}
	
	private ArrayList<GoldCard> getCardsToValue(int value){
		//TODO Improve this method
		ArrayList<GoldCard> result = new ArrayList<>();
		boolean finished;
		switch (value){
			case 0:
				break;
			case 1:
				for (GoldCard card : goldCardStack){
					if (card.getValue() == 1){
						result.add(card);
						goldCardStack.remove(card);
						break;
					}
				}
				break;
			case 2:
				finished = false;
				for (GoldCard card : goldCardStack){
					if (card.getValue() == 2){
						result.add(card);
						goldCardStack.remove(card);
						finished = true;
						break;
					}
				}
				if (!finished){
					result.add(getCardsToValue(1).get(0));
					result.add(getCardsToValue(1).get(0));
				}

				break;
			case 3:
				finished = false;
				for (GoldCard card : goldCardStack){
					if (card.getValue() == 3){
						result.add(card);
						goldCardStack.remove(card);
						finished = true;
						break;
					}
				}
				if (!finished){
					for (GoldCard card : getCardsToValue(2)){
						result.add(card);
						goldCardStack.remove(card);
					}
					result.add(getCardsToValue(1).get(0));
				}
				break;
			case 4:
				ArrayList<GoldCard> ofValueOne = getCardsToValue(1);
				if (!ofValueOne.isEmpty()){
					for (GoldCard card : getCardsToValue(3)){
						result.add(card);
						goldCardStack.remove(card);
					}
					result.add(ofValueOne.get(0));
				} else {
					for (GoldCard card : getCardsToValue(2)){
						result.add(card);
						goldCardStack.remove(card);
					}
					for (GoldCard card : getCardsToValue(2)){
						result.add(card);
						goldCardStack.remove(card);
					}
				}

				break;
			default:
				System.err.println("Impossible to be here");		
		}
		
		return result;
	}

	private void setTeam(){
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

		Collections.shuffle(team, new Random(Game.seed));

		for (Player aPlayerList : this.playerList) {
			Team role = team.remove(0);
			aPlayerList.setTeam(role);
		}
	}

	public Player getWinner() {
		int maxGold = 0;
		Player winner = null;
		for(Player p : this.playerList){
			if(p.getGold()>maxGold){
				maxGold = p.getGold();
				winner = p;
			}
		}
		return winner;
	}
}
