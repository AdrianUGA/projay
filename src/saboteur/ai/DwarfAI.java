package saboteur.ai;

import saboteur.model.Game;

public class DwarfAI extends AI {
	
	public DwarfAI(Game game){
		super(game);
	}
	
	public void selectCard(){
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
		
		
	}
	
	private void selectCardMediumAI() {
		// TODO Auto-generated method stub
		
	}

	private void selectCardHardAI() {
		// TODO Auto-generated method stub
		
	}
	
}
