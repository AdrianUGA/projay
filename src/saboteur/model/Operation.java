package saboteur.model;

import saboteur.model.Card.Card;

public abstract class Operation {
	private Player sourcePlayer; //TO SAVE
	private Card card; //TO SAVE
	
	public Operation(Player sourcePlayer, Card card){
		this.sourcePlayer = sourcePlayer;
		this.card = card;
	}
	
	public abstract void exec(Game game);
	public abstract void execReverse(Game game);
	
	public Card getCard(){
		return card;
	}
	
	public Player getSourcePlayer(){
		return this.sourcePlayer;
	}

	public Operation setSourcePlayer(Player sourcePlayer) {
		this.sourcePlayer = sourcePlayer;
		return this;
	}

	public Operation setCard(Card card) {
		this.card = card;
		return this;
	}
}
