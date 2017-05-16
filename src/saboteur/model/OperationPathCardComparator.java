package saboteur.model;

import java.util.Comparator;

public class OperationPathCardComparator implements Comparator<OperationPathCard>{
	PositionComparator positionComparator;

	public OperationPathCardComparator(PositionComparator positionComparator) {
		super();
		this.positionComparator = positionComparator;
	}

	@Override
	public int compare(OperationPathCard p1, OperationPathCard p2) {
		return this.positionComparator.compare(p1.getP(), p2.getP());
	}
	
	
	
}
