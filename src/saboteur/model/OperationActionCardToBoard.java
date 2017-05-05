package saboteur.model;

import saboteur.model.Card.*;

public class OperationActionCardToBoard extends Operation {
	private PathCard destinationCard;
	
	public OperationActionCardToBoard(Player sourcePlayer, Card card, PathCard destinationCard) {
		super(sourcePlayer, card);
		this.destinationCard = destinationCard;
	}

	@Override
	public void exec(Game game) {
		// TODO Auto-generated method stub
		//Execution different if destinationCard is a goal or a classic pathCard
	}
}
