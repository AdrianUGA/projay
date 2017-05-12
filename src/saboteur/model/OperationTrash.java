package saboteur.model;

import saboteur.model.Card.Card;

public class OperationTrash extends Operation {

	public OperationTrash(Player sourcePlayer, Card card) {
		super(sourcePlayer, card);
	}

	@Override
	public void exec(Game game) {
		this.getSourcePlayer().removeHandCard(this.getCard());
	}
	
	@Override
	public void execReverse(Game game) {
		this.getSourcePlayer().addHandCard(this.getCard());
	}

}
