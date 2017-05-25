package saboteur.model.Card;

public class RescueCard extends ActionCardToPlayer {

	private static final long serialVersionUID = 3465799595171860044L;
	private Tool rescueType;
	
	public RescueCard(Tool type){
		this.rescueType = type;
	}
	
	public Tool getRescueType(){
		return this.rescueType;
	}

	public RescueCard clone(){
		RescueCard card;

		card = (RescueCard) super.clone();

		card.rescueType = rescueType;

		return card;
	}
	
	public Tool getTool(){
		return rescueType;
	}
	
	@Override
	public boolean isRescueCard() {
		return true;
	}
	
	@Override
	public void displayCardType(){
    	System.out.println("RescueCard");
    }
	
	@Override
	public String toString() {
		return "RescueCard : " + this.getRescueType();
	}
}
