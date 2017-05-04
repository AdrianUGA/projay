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

	public void setHandicaps(ArrayList<ActionCard> handicaps) {
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
	private ArrayList<ActionCard> handicaps;
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
	
	public void playCard(){
		this.playCard(this.selectedCard);
	}
	
	public ArrayList<ActionCard> getHandicaps(){
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
	
	public void addHandicapCard(ActionCard card){
		this.handicaps.add(card);
	}
	
	public void removeGoldCard(GoldCard card){
		this.gold.remove(card);
	}
	
	public boolean canHandicap(ActionCard card){
		//TODO
		return false;
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
