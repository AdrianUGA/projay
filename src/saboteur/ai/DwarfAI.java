package saboteur.ai;


public class DwarfAI extends AI {
	
	private Difficulty difficulty;

	public DwarfAI(){
		
	}
	
	public DwarfAI setDifficulty(Difficulty difficulty){
		this.difficulty = difficulty;
		return this;
	}
}
