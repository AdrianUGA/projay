package saboteur.model.Card;

public class PlanCard extends ActionCardToBoard {

	private static final long serialVersionUID = 3449539183494327840L;

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
	
	@Override
	public String toString() {
		return "PlanCard";
	}
}
