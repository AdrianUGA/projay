package saboteur.ai;

import saboteur.model.Game;
import saboteur.model.Team;

public class TemporarAI extends AI {
	Game game;
	public TemporarAI(Game game, String name) {
		super(game, name);
		this.game = game;
	}
	
	public AI getNewAI(Team team){
		AI newAI = null;
		if (team == Team.DWARF){
			newAI = new DwarfAI(this.game, "test");
			newAI.setName(this.name);
			newAI.setTeam(this.team);
		}else if (team == Team.SABOTEUR){
			newAI = new SaboteurAI(this.game, "test");
			newAI.setName(this.name);
			newAI.setTeam(this.team);
		}else{
			System.err.println("Not supposed to happen");
		}
		return newAI;
	}

	@Override
	protected void computeOperationWeightHardAI() {
		return;
	}

	@Override
	protected void computeOperationWeightMediumAI() {
		return;
	}

	@Override
	protected void computeOperationWeightEasyAI() {
		return;
	}
	
}
