package saboteur.model;

import saboteur.model.Card.Card;

public abstract class Operation {
	private Player sourcePlayer;
	private Card card;
	
	public void exec(){
		
	}
	
	public Card getCard(){
		return card;
	}
}
