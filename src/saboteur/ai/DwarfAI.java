package saboteur.ai;

import saboteur.model.Game;

public class DwarfAI extends AI {
	
	private Difficulty difficulty;

	public DwarfAI(Game game){
		super(game);
	}
	
	public DwarfAI setDifficulty(Difficulty difficulty){
		this.difficulty = difficulty;
		return this;
	}
}
