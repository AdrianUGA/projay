package saboteur.model;

import java.util.ArrayList;

import saboteur.model.Card.*;

public abstract class Player {
	private boolean saboteur;	
	public Card getSelectedCard() {
		return selectedCard;
	}

	public void setSelectedCard(Card selectedCard) {
		this.selectedCard = selectedCard;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public void setSaboteur(boolean saboteur) {
		this.saboteur = saboteur;
	}

	public void setHandicaps(ArrayList<SabotageCard> handicaps) {
		this.handicaps = handicaps;
	}

	public void setGold(ArrayList<GoldCard> gold) {
		this.gold = gold;
	}

	public void setHand(ArrayList<Card> hand) {
		this.hand = hand;
	}

	private Card selectedCard;
	private String nom;
	private ArrayList<SabotageCard> handicaps;
	private ArrayList<GoldCard> gold;
	private ArrayList<Card> hand;
	private Game game;
	
	public Player (Game game){
		this.game = game;
		game.register(this);
	}
	
	public boolean isSaboteur(){
		return this.saboteur;
	}
	
	public void playCard(Card card){
	}
	public void playCard(Player destinationPlayer){
		Operation operation = new OperationActionCardToPlayer(this, this.selectedCard, destinationPlayer);
		
		this.game.playOperation(operation);
	}
	public void playCard(PathCard destinationCard){
		Operation operation = new OperationActionCardToBoard(this, this.selectedCard, destinationCard);
		
		this.game.playOperation(operation);
	}
	public void playCard(Position position){
		Operation operation = new OperationPathCard(this, this.selectedCard, position);
		
		this.game.playOperation(operation);
	}
	
	public void playCard(){
		Operation operation = new OperationTrash(this, this.selectedCard);
		
		this.game.playOperation(operation);
	}
	
	
	
	public ArrayList<SabotageCard> getHandicaps(){
		return this.handicaps;
	}
	
	public int getGold(){
		int total = 0;
		for (GoldCard card : this.gold){
			total += card.getValue();
		}
		return total;
	}
	
	public ArrayList<Card> getHand(){
		return this.hand;
	}
	
	public void removeHandCard(Card card){
		this.hand.remove(card);
	}
	
	public void removeHandicapCard(ActionCard card){
		this.handicaps.remove(card);
	}
	
	public void addHandCard(Card card){
		this.hand.add(card);
	}
	
	public void addHandicapCard(SabotageCard card){
		this.handicaps.add(card);
	}
	
	public void removeGoldCard(GoldCard card){
		this.gold.remove(card);
	}
	
	public boolean canHandicap(ActionCardToPlayer card){
		System.out.println("On ne doit pas passer la ! (N1)");
		return false;
	}
	public boolean canHandicap(DoubleRescueCard card){
		int currentType;
		for (SabotageCard sabotageCard : this.handicaps){
			currentType = sabotageCard.getSabotageType();
			if (currentType == card.getRescueType1() || currentType == card.getRescueType2()){
				return true;
			}
		}
		return false;
	}
	public boolean canHandicap(RescueCard card){
		for (SabotageCard sabotageCard : this.handicaps){
			if (sabotageCard.getSabotageType() == card.getRescueType()){
				return true;
			}
		}
		return false;
	}
	public boolean canHandicap(SabotageCard card){
		for (SabotageCard sabotageCard : this.handicaps){
			if (sabotageCard.getSabotageType() == card.getSabotageType()){
				return false;
			}
		}
		return true;
	}

	public String getNom() {
		return nom;
	}

	public Player setNom(String nom) {
		this.nom = nom;
		return this;
	}
	
	public void notify(Operation o){
		
	}
}
