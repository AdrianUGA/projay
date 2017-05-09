package saboteur.ai;

import saboteur.model.Game;

public class DwarfAI extends AI {
	
	public DwarfAI(Game game){
		super(game);
	}
	
	public void selectCard(){
		resetProbabilitiesToPlayEachCard();
		switch(this.getDifficulty()){
		case easy:
			selectCardEasyAI();
			break;
		case medium:
			selectCardMediumAI();
			break;
		case hard:
			selectCardHardAI();
			break;
		}
	}
	
	private void selectCardEasyAI() {
		if(knowsTheGoldCardPosition()){
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
		
		
	}
	
	private void selectCardMediumAI() {
		// TODO Auto-generated method stub
		
	}

	private void selectCardHardAI() {
		// TODO Auto-generated method stub
		
	}
	
}
