package saboteur.model;

import java.util.Comparator;

public class PositionComparator implements Comparator<Position>{
	
	private Position toCompare;
	
	public PositionComparator (Position toCompare){
		this.toCompare = toCompare;
	}
	
	@Override
	public int compare(Position p1, Position p2){
		return this.toCompare.getTaxiDistance(p1) - this.toCompare.getTaxiDistance(p2);
	}
}
