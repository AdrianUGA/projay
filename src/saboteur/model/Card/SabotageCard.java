package saboteur.model.Card;

public class SabotageCard extends ActionCardToPlayer {

	private static final long serialVersionUID = -8851818492796619397L;
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
	
	public Tool getTool(){
		return sabotageType;
	}
	
	@Override
	public boolean isSabotageCard() {
		return true;
	}
	
	@Override
	public void displayCardType(){
    	System.out.println("SabotageCard");
    }
}
