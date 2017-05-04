package saboteur.model;

import saboteur.model.Card.Card;

public class OperationPathCard extends Operation {
	Position position;
	
	public OperationPathCard(Player sourcePlayer, Card card, Position position) {
		super(sourcePlayer, card);
		this.position = position;
	}

	public Position getPosition(){
		return this.position;
	}
}
