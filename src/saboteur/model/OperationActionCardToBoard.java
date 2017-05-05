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
		this.getSourcePlayer().removeHandCard(this.getCard());
		if (destinationCard.isGoal()){
			//TODO
		} else {
			Position positionCard = new Position(4,4);//TODO use a method to find position of a PathCard
			
			game.getBoard().removeCard(positionCard);
		}
		//Execution different if destinationCard is a goal or a classic pathCard
	}
}
