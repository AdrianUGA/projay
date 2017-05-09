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
		
		//Execution different if destinationCard is a goal or a classic pathCard
		if (destinationCard.isGoal()){
			this.getSourcePlayer().viewGoalCard();
		} else {
			Position positionCard = game.getBoard().getPositionCard(destinationCard);		
			game.getBoard().removeCard(positionCard);
		}
	}
}
