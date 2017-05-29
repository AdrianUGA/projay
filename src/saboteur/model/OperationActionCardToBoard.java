package saboteur.model;

import saboteur.model.Card.*;

public class OperationActionCardToBoard extends Operation {

	private static final long serialVersionUID = 1105275240537798093L;
	private PathCard destinationCard; //TO SAVE
	private Position positionDestination; //TO SAVE
	
	public OperationActionCardToBoard(Player sourcePlayer, Card card, PathCard destinationCard) {
		super(sourcePlayer, card);
		this.destinationCard = destinationCard;
	}

	@Override
	public void exec(Game game) {
		ActionCardToBoard sourceCard = (ActionCardToBoard) this.getCard();
		this.getSourcePlayer().removeHandCard(this.getCard());
		
		//Execution different if actionCard is a collapseCard or a planCard
		this.positionDestination = game.getBoard().getPosition(destinationCard);
		if (sourceCard.isCollapseCard()){
			if (!destinationCard.isStart() && !destinationCard.isGoal()){
				game.getBoard().removeCard(this.positionDestination);
			}
		} else {
			if (destinationCard.isGoal()){
				this.getSourcePlayer().viewGoalCard(destinationCard);
			}
		}
		
		game.notify(this);
	}
	
	@Override
	public void execReverse(Game game) {
		ActionCardToBoard sourceCard = (ActionCardToBoard) this.getCard();
		this.getSourcePlayer().addHandCard(this.getCard());
		
		//Execution different if actionCard is a collapseCard or a planCard
		if (sourceCard.isCollapseCard()){
			if (!destinationCard.isStart() && !destinationCard.isGoal()){	
				game.getBoard().addCard(destinationCard, this.positionDestination);
			}
		}
		
	}

	public PathCard getDestinationCard() {
		return destinationCard;
	}

	public OperationActionCardToBoard setDestinationCard(PathCard destinationCard) {
		this.destinationCard = destinationCard;
		return this;
	}
	
	public Position getPositionDestination(){
		return this.positionDestination;
	}
	
	public void setPositionDestination(Position positionDestination){
		this.positionDestination = positionDestination;
	}
	
	@Override
	public boolean isOperationActionCardToBoard(){
		return true;
	}
	
	@Override
	public void displayOperationInformation(){
		System.out.print("OperationActionCardToBoard : destination cX = " + this.getPositionDestination().getcX() + " cY = " + this.getPositionDestination().getcY());
	}
	
}
