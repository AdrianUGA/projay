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
		ActionCardToBoard sourceCard = (ActionCardToBoard) this.getCard();
		this.getSourcePlayer().removeHandCard(this.getCard());
		
		//Execution different if actionCard is a collapseCard or a planCard
		if (sourceCard.isCollapse()){
			if (!destinationCard.isStart() && !destinationCard.isGoal()){
				Position positionCard = game.getBoard().getPositionCard(destinationCard);		
				game.getBoard().removeCard(positionCard);
			}
		} else {
			if (destinationCard.isGoal()){
				this.getSourcePlayer().viewGoalCard(destinationCard);
			}
		}
	}
}
