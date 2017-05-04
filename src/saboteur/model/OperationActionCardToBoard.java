package saboteur.model;

import saboteur.model.Card.*;

public class OperationActionCardToBoard extends Operation {
	private PathCard destinationCard;
	
	public OperationActionCardToBoard(Player sourcePlayer, Card card, PathCard destinationCard) {
		super(sourcePlayer, card);
		this.destinationCard = destinationCard;
	}
}
