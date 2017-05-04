package saboteur.model;

public class OperationPathCard extends Operation {
	private Position p;
	
	public Position getP() {
		return p;
	}

	public OperationPathCard(Position pos){
		p = pos;
	}
}
