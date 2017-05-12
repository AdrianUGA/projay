package saboteur.model;

import saboteur.model.Card.*;

public class OperationPick extends Operation {

	private Card cardPicked;
	
	public OperationPick(Player sourcePlayer) {
		super(sourcePlayer, null);
	}
	
	@Override
	public void exec(Game game) {
		this.cardPicked = game.pick();
		
		this.getSourcePlayer().addHandCard(this.cardPicked);
	}

	@Override
	public void execReverse(Game game) {
		game.addCardToStack(this.cardPicked);
		
		this.getSourcePlayer().removeHandCard(this.cardPicked);
	}

}
