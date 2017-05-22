package saboteur.ai;

import saboteur.model.Team;

public abstract class AIComputer {
	
	
	private Team team;

	public AIComputer setTeam(Team team) {
		this.team = team;
		return this;
	}

}
