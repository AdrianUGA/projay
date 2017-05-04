package saboteur.model;

import saboteur.model.Card.Card;

public abstract class Operation {
	private Player sourcePlayer;
	private Card card;
	
	public Operation(Player sourcePlayer, Card card){
		this.sourcePlayer = sourcePlayer;
		this.card = card;
	}
	
	public void exec(){
		
	}
	
	public Card getCard(){
		return card;
	}
	
	public Player getSourcePlayer(){
		return this.sourcePlayer;
	}
}
