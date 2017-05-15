package saboteur.model.Card;

import java.util.LinkedList;
import java.util.List;

public class PathCard extends Card {
	//OPENUP(1), OPENRIGHT(2), OPENDOWN(4), OPENLEFT(8));
	
	private List<Cardinal> openSides;
    private final boolean isCulDeSac;
    private final boolean isGoal;
    private final boolean isStart;
    private final boolean hasGold;
    private boolean isVisible;

	public PathCard(String[] cardinal, boolean isCulDeSac, boolean isStart, boolean isGoal, boolean hasGold){
        this.openSides = new LinkedList<>();
        for (String c: cardinal) {
            this.openSides.add(Cardinal.valueOf(c));
        }
        this.hasGold = hasGold;
        this.isCulDeSac = isCulDeSac;
        this.isStart = isStart;
        this.isGoal = isGoal;
        this.isVisible = true;
	}
	
	public boolean isStart() {
		return isStart;
	}
	
	public PathCard setVisible(boolean visible){
		this.isVisible = visible;
		return this;
	}
	
	public boolean isGoal(){
		return this.isGoal;
	}
	
	public boolean isVisible(){
		return this.isVisible;
	}
	
	public boolean hasGold(){
		return this.hasGold;
	}
	
	public boolean isCulDeSac(){
		return (this.isCulDeSac);
	}
	
	public PathCard reversed(){
		List<Cardinal> openSides = new LinkedList<>();
		for(Cardinal cardinal : this.openSides){
			openSides.add(cardinal.opposite());
		}
		
		try {
			return ((PathCard) this.clone()).setOpenSides(openSides);
		} catch (CloneNotSupportedException e) {
			System.err.println("Impossible to clone pathcard. That is NOT supposed to happen, like ever");
			e.printStackTrace();
		}
		return null; /* that never happen */
	}

	private PathCard setOpenSides(List<Cardinal> openSides) {
		this.openSides = openSides;
		return this;
	}
	
	public boolean isOpen(Cardinal cardinal){
		for (Cardinal current : this.openSides){
			if (current.getValue() == cardinal.getValue()) return true;
		}
		return false;
	}
	
	public int openSidesAmount(){
		return this.openSides.size();
	}

	@Override
	public String toString() {
		return "Paths : " + this.openSides;
	}

	
}
