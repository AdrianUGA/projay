package saboteur.model;

import saboteur.model.Card.Card;

public class OperationActionCardToPlayer extends Operation {
	private Player destinationPlayer;
	
	public OperationActionCardToPlayer(Player sourcePlayer, Card card, Player destinationPlayer) {
		super(sourcePlayer, card);
		this.destinationPlayer = destinationPlayer;
	}
	
	public Player getDestinationPlayer(){
		return destinationPlayer;
	}
}
