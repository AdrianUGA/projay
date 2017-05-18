package saboteur.model.Card;

public class PlanCard extends ActionCardToBoard {

	@Override
	public boolean isCollapse() {
		return false;
	}

	@Override
	public boolean isPlanCard() {
		return true;
	}
	
	@Override
	public void displayCardType(){
    	System.out.println("PlanCard");
    }
	
}
