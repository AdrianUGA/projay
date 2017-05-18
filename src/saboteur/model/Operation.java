package saboteur.model;

import java.io.Serializable;

import saboteur.model.Card.Card;

public abstract class Operation implements Serializable {

	private static final long serialVersionUID = -1654062965339840406L;
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

	@Override
	public boolean equals(Object obj) {
		Operation o = (Operation) obj;
		return o.getCard() == this.card && o.getSourcePlayer().equals(this.sourcePlayer);
	}
	
	public boolean isOperationActionCardToBoard(){
		return false;
	}
	
	public boolean isOperationActionCardToPlayer(){
		return false;
	}
	
	public boolean isOperationPathCard(){
		return false;
	}
	
	public boolean isOperationPick(){
		return false;
	}
	
	public boolean isOperationTrash(){
		return false;
	}
	
	public void displayOperationInformation(){
		System.out.print("OperationNonReconnue");
	}
	
}
