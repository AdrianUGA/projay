package saboteur.model;

import java.io.Serializable;

import saboteur.model.Card.Card;

public abstract class Operation implements Serializable {

	private static final long serialVersionUID = -1654062965339840406L;
	private Player sourcePlayer;
	private Card card;
	private int indexOfCardInHandPlayer = -1;

	public Operation(Player sourcePlayer, Card card){
		this.sourcePlayer = sourcePlayer;
		this.card = card;
		if (sourcePlayer != null && card != null){
			this.indexOfCardInHandPlayer = this.sourcePlayer.getHand().indexOf(this.card);
		}
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

	public int getIndexOfCardInHandPlayer(){
		return this.indexOfCardInHandPlayer;
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
