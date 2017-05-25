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

	public SabotageCard clone(){
		SabotageCard card;

		card = (SabotageCard) super.clone();

		card.sabotageType = sabotageType;

		return card;
	}
	
	@Override
	public boolean isSabotageCard() {
		return true;
	}
	
	@Override
	public void displayCardType(){
    	System.out.println("SabotageCard");
    }
	
	@Override
	public String toString() {
		return "SabotageCard : " + this.getSabotageType();
	}
}
