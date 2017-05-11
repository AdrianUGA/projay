package saboteur.model;

import saboteur.model.Card.*;

public class OperationActionCardToPlayer extends Operation {
	private Player destinationPlayer;
	
	public OperationActionCardToPlayer(Player sourcePlayer, Card card, Player destinationPlayer) {
		super(sourcePlayer, card);
		this.destinationPlayer = destinationPlayer;
	}
	
	public Player getDestinationPlayer(){
		return destinationPlayer;
	}

	@Override
	public void exec(Game game) {
		this.getSourcePlayer().removeHandCard(this.getCard());
		destinationPlayer.addHandicapCard((SabotageCard)this.getCard());
	}

	public OperationActionCardToPlayer setDestinationPlayer(Player destinationPlayer) {
		this.destinationPlayer = destinationPlayer;
		return this;
	}
	
	
}
