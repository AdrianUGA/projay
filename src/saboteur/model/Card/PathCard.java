package saboteur.model.Card;

import java.util.LinkedList;
import java.util.List;

public class PathCard extends Card {
	//OPENUP(1), OPENRIGHT(2), OPENDOWN(4), OPENLEFT(8));
	
	List<Cardinal> openSides;
	final boolean isCulDeSac;
	boolean isGoal;
	boolean isStart;
	boolean hasGold;
	boolean isVisible;
	
	public PathCard(int i){
		this.isVisible=true;
		this.hasGold = false;
		this.isGoal = false;
		this.isStart = false;
		this.openSides = new LinkedList<Cardinal>();
		if(i>=16){
			isCulDeSac = true;
			i=i-16;
		}else{
			isCulDeSac = false;
		}
		if(i>=8){
			this.openSides.add(Cardinal.WEST);
			i=i-8;
		}
		if(i>=4){
			this.openSides.add(Cardinal.SOUTH);
			i=i-4;
		}
		if(i>=2){
			this.openSides.add(Cardinal.EAST);
			i=i-2;
		}
		if(i>=1){
			this.openSides.add(Cardinal.NORTH);
		}
	}
	
	public boolean isStart() {
		return isStart;
	}

	public PathCard setStart(boolean isStart) {
		this.isStart = isStart;
		return this;
	}

	public PathCard setToGoal(){
		this.isGoal = true;
		return this;
	}
	
	public PathCard setVisible(boolean visible){
		this.isVisible = visible;
		return this;
	}
	
	public PathCard setToGold(){
		this.hasGold = true;
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
	
	public PathCard(List<Cardinal> openSides, boolean isCulDeSac){
		this.openSides = openSides;
		this.isCulDeSac = isCulDeSac;
	}
	
	public boolean isCulDeSac(){
		return (this.isCulDeSac);
	}
	
	public PathCard reversed(){
		List<Cardinal> openSides = new LinkedList<Cardinal>();
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
}
