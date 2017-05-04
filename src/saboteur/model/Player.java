package saboteur.model;

import java.util.ArrayList;

import saboteur.model.Card.*;

public abstract class Player {
	private boolean saboteur;	
	private Card selectedCard;
	private String nom;
	private ArrayList<ActionCard> handicaps;
	private ArrayList<GoldCard> gold;
	private ArrayList<Card> hand;
	
	public boolean isSaboteur(){
		return this.saboteur;
	}
	
	public void playCard(){
		//TODO use selectedCard
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
}
