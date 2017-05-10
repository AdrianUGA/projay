package saboteur.model.Card;

public class SabotageCard extends ActionCardToPlayer {
	private Tool sabotageType;
	
	public SabotageCard(Tool type){
		this.sabotageType = type;
	}
	
	public Tool getSabotageType(){
		return this.sabotageType;
	}
}
