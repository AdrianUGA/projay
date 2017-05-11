package saboteur.model;

import java.util.ArrayList;

import saboteur.model.Card.*;

public abstract class Player {
	protected boolean saboteur;
	protected Card selectedCard;
	protected String nom;
	protected ArrayList<SabotageCard> handicaps;
	protected ArrayList<GoldCard> gold;
	protected ArrayList<Card> hand;
	protected Game game;
	
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
	
	public boolean canRescue(ActionCardToPlayer card){
		System.err.println("Invalid Card. That is NOT supposed to happen, like ever");
		return false;
	}
	public boolean canRescueWithDoubleRescueCard(DoubleRescueCard card){
		Tool currentType;
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

	//Human : useless
	//IA : redefinition
	public void viewGoalCard(PathCard card){
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
