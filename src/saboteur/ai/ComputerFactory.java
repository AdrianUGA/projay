package saboteur.ai;

import saboteur.model.Team;

public class ComputerFactory {
	
	public static Computer getComputer(AI artificialIntelligence){
		// TODO to remove
		if(artificialIntelligence.getTeam() == Team.DWARF)
			return getComputer(Difficulty.HARD, Team.DWARF).setArtificialIntelligence(artificialIntelligence);
		return getComputer(Difficulty.EASY, Team.SABOTEUR).setArtificialIntelligence(artificialIntelligence);
		//
		
		/*
		return getComputer(
				artificialIntelligence.getDifficulty(),
				artificialIntelligence.getTeam())
				.setArtificialIntelligence(artificialIntelligence);
				*/
	}
	
	private static Computer getComputer(Difficulty difficulty, Team team){
		switch (difficulty) {
		case EASY:
			return (team == Team.DWARF ? new EasyDwarfComputer() : new EasySaboteurComputer());
		case MEDIUM:
			return (team == Team.DWARF ? new MediumDwarfComputer() : new MediumSaboteurComputer());
		case HARD:
			return (team == Team.DWARF ? new HardDwarfComputer() : new HardSaboteurComputer());
		default:
			System.err.println("Not supposed to happen");
			return null;
		}
	}
}
