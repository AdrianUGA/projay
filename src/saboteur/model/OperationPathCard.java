package saboteur.model;

import saboteur.model.Card.Card;

public class OperationPathCard extends Operation {
	private Position p;
	
	public Position getP() {
		return p;
	}

	public OperationPathCard(Player sourcePlayer, Card card, Position position) {
		super(sourcePlayer, card);
		this.p = position;
	}
}
