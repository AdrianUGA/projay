package saboteur.model;

public class Human extends Player {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5780673417385421937L;

	public Human(Game game, String name) {
		super(game, name);
		// TODO Auto-generated constructor stub
	}

	public boolean isHuman(){
		return true;
	}
}
