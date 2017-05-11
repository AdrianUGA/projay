package saboteur.model;

import saboteur.model.Card.*;

public class OperationPathCard extends Operation {
	private Position p;
	
	public Position getP() {
		return p;
	}

	public OperationPathCard(Player sourcePlayer, Card card, Position position) {
		super(sourcePlayer, card);
		this.p = position;
	}

	@Override
	public void exec(Game game) {
		this.getSourcePlayer().removeHandCard(this.getCard());
		game.getBoard().addCard((PathCard)this.getCard(), p);
	}

	public OperationPathCard setP(Position p) {
		this.p = p;
		return this;
	}
	
	
}
