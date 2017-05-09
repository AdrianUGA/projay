package saboteur.model.Card;

public class SabotageCard extends ActionCardToPlayer {
	private int sabotageType;
	
	public SabotageCard(int type){
		this.sabotageType = type;
	}
	
	public int getSabotageType(){
		return this.sabotageType;
	}
}
