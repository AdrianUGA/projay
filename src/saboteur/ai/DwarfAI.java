package saboteur.ai;

import saboteur.model.Game;
import saboteur.model.Operation;
import saboteur.model.OperationActionCardToBoard;
import saboteur.model.OperationActionCardToPlayer;
import saboteur.model.Player;
import saboteur.model.Card.Card;
import saboteur.model.Card.PathCard;
import saboteur.model.Card.RescueCard;
import saboteur.model.Card.DoubleRescueCard;

public class DwarfAI extends AI {
	
	public DwarfAI(Game game){
		super(game);
	}
	
	public void selectCard(){
		resetProbabilitiesToPlayEachCard();
		switch(this.getDifficulty()){
		case EASY:
			computeCardWeightEasyAI();
			break;
		case MEDIUM:
			computeCardWeightMediumAI();
			break;
		case HARD:
			computeCardWeightHardAI();
			break;
		}
		//TODO choose heaviest card
	}
	
	private void computeCardWeightEasyAI() {
		for(Operation o : operationsWeight.keySet()){
			switch(o.getCard().getClassName()){
			case "PlanCard":
				if(!knowsTheGoldCardPosition()){
					((OperationActionCardToBoard) o).setDestinationCard(getGame().getBoard().getCard(getEstimatedGoldCardPosition()));
					operationsWeight.put(o, (1 + positiveOrZero(Coefficients.DWARF_PLAN_TURN_EASY - getGame().getTurn()))
											* Coefficients.DWARF_PLAN_EASY);
				}
				else{
					operationsWeight.put(o, 0);
				}
				break;
			case "RescueCard":
				if(canRescue((RescueCard)o.getCard())){
					((OperationActionCardToPlayer) o).setDestinationPlayer(this);
				}
				
			}
		}
		
	}
	
	private void computeCardWeightMediumAI() {
		// TODO Auto-generated method stub
		
	}

	private void computeCardWeightHardAI() {
		// TODO Auto-generated method stub
		
	}

}
