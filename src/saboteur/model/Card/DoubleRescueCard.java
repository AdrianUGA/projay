package saboteur.model.Card;

public class DoubleRescueCard extends ActionCardToPlayer {

	private static final long serialVersionUID = -1679910142473555687L;
	public Tool rescueType1;
	public Tool rescueType2;
	
	public DoubleRescueCard(Tool type1, Tool type2){
		this.rescueType1 = type1;
		this.rescueType2 = type2;
	}

	public Tool getRescueType1() {
		return this.rescueType1;
	}

	public Tool getRescueType2() {
		return this.rescueType2;
	}

	@Override
	public ActionCardToPlayerType getType() {
		return ActionCardToPlayerType.DOUBLE_RESCUE;
	}

	public DoubleRescueCard clone(){
		DoubleRescueCard card;

		card = (DoubleRescueCard) super.clone();

		card.rescueType1 = rescueType1;
		card.rescueType2 = rescueType2;

		return card;
	}
	
	public Tool getTool1(){
		return rescueType1;
	}
	
	public Tool getTool2(){
		return rescueType2;
	}
	
	@Override
	public boolean isDoubleRescueCard() {
		return true;
	}
	
	@Override
	public void displayCardType(){
    	System.out.println("DoubleRescueCard");
    }
}
