package saboteur.model.Card;

public class CollapseCard extends ActionCardToBoard {

	@Override
	public boolean isCollapse() {
		return true;
	}
	
	@Override
	public boolean isCollapseCard() {
		return true;
	}
	
}
