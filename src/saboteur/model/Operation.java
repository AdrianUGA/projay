package saboteur.model;

import saboteur.model.Card.Card;

public abstract class Operation {
	private Player sourcePlayer;
	private Card card;
	
	public Operation(Player sourcePlayer, Card card){
		this.sourcePlayer = sourcePlayer;
		this.card = card;
	}
	
	public abstract void exec(Game game);
	
	public Card getCard(){
		return card;
	}
	
	public Player getSourcePlayer(){
		return this.sourcePlayer;
	}
	
}
