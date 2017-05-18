package saboteur.model.Card;

public class CollapseCard extends ActionCardToBoard {

	private static final long serialVersionUID = 7675543937932678336L;

	@Override
	public boolean isCollapse() {
		return true;
	}
	
	@Override
	public boolean isCollapseCard() {
		return true;
	}
	
	@Override
	public void displayCardType(){
    	System.out.println("CollapseCard");
    }
	
}
