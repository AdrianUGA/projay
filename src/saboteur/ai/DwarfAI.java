package saboteur.ai;

import saboteur.model.Game;
import saboteur.model.Operation;
import saboteur.model.OperationActionCardToBoard;
import saboteur.model.Card.Card;
import saboteur.model.Card.PathCard;

public class DwarfAI extends AI {
	
	public DwarfAI(Game game){
		super(game);
	}
	
	public void selectCard(){
		resetProbabilitiesToPlayEachCard();
		switch(this.getDifficulty()){
		case easy:
			computeCardWeightEasyAI();
			break;
		case medium:
			computeCardWeightMediumAI();
			break;
		case hard:
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
					;
					this.getGame().getBoard().getPositionCard(pc);//OperationActionCardToBoard.getDestinationCard().;
					operationsWeight.put(o, (1 + positiveOrZero(3 - getGame().getTurn())) * Coefficients.DWARF_PLAN_EASY);
				}
				else{
					operationsWeight.put(o, 0);
				}
				break;
			case "RescueCard":
				
			}
		}
		
		/*if(knowsTheGoldCardPosition()){
			// Proba of playing plan = 0
		}
		else{ 
			if(getGame().getTurn()<2){
			// Proba of playing plan +++
			}
			else if(getGame().getTurn()<4){
				// Proba of playing plan ++
			}
			else if(getGame().getTurn()<6){
				// Proba of playing plan +
			}
		}
		if(true){ // if can play any path card which isn't cul-de-sac
			// Proba of playing pathCard ++
		}
		// Proba of destroying random card + (turn > 3)
		// Proba of put a bit "randomly" a sabotage card + (4 >= turn >= 2)
		//				a bit "precisely" a sabotage card + (7 >= turn >= 5)
		//				very "precisely" a sabotage card + (turn > 8)
		*/
		
	}
	
	private void computeCardWeightMediumAI() {
		// TODO Auto-generated method stub
		
	}

	private void computeCardWeightHardAI() {
		// TODO Auto-generated method stub
		
	}

}
