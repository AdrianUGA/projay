package saboteur.model.Card;

public class RescueCard extends ActionCardToPlayer {
	private int rescueType;
	
	public RescueCard(int type){
		this.rescueType = type;
	}
	
	public int getRescueType(){
		return this.rescueType;
	}
}
