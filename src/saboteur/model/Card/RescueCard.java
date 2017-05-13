package saboteur.model.Card;

public class RescueCard extends ActionCardToPlayer {
	private Tool rescueType;
	
	public RescueCard(Tool type){
		this.rescueType = type;
	}
	
	public Tool getRescueType(){
		return this.rescueType;
	}

	@Override
	public ActionCardToPlayerType getType() {
		return ActionCardToPlayerType.RESCUE;
	}
	
}
