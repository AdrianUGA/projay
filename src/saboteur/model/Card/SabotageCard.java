package saboteur.model.Card;

public class SabotageCard extends ActionCardToPlayer {
	private Tool sabotageType;
	
	public SabotageCard(Tool type){
		this.sabotageType = type;
	}
	
	public Tool getSabotageType(){
		return this.sabotageType;
	}

	@Override
	public ActionCardToPlayerType getType() {
		return ActionCardToPlayerType.SABOTAGE;
	}

	public SabotageCard clone(){
		SabotageCard card;

		card = (SabotageCard) super.clone();

		card.sabotageType = sabotageType;

		return card;
	}
}
