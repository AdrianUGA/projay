package saboteur.ai;

import saboteur.model.Game;
import saboteur.model.Team;

public class TemporarAI extends AI {
	Game game;
	public TemporarAI(Game game) {
		super(game);
		this.game = game;
		
	}
	
	public AI setTeam(Team team){
		if (team == Team.DWARF){
			return new DwarfAI(this.game);
		}else if (team == Team.SABOTEUR){
			return new SaboteurAI(this.game);
		}else{
			System.err.println("Not supposed to happen");
		}
		return null;
	}
	
}
