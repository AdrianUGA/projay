package saboteur.model;

public class Human extends Player {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5780673417385421937L;

	public Human(Game game) {
		super(game, "human");
		// TODO Auto-generated constructor stub
	}

	public boolean isHuman(){
		return true;
	}
}
