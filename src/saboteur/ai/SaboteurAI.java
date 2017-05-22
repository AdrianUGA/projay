package saboteur.ai;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import saboteur.model.Operation;
import saboteur.model.OperationActionCardToBoard;
import saboteur.model.OperationActionCardToPlayer;
import saboteur.model.OperationPathCard;
import saboteur.model.OperationTrash;
import saboteur.model.Player;
import saboteur.model.Position;
import saboteur.model.Card.DoubleRescueCard;
import saboteur.model.Card.PathCard;
import saboteur.model.Card.RescueCard;
import saboteur.model.Card.SabotageCard;

public abstract class SaboteurAI {
	
	public static void computeOperationWeightEasyAI(AI artificialIntelligence) {
		AIComputer aIComputer = getComputer(artificialIntelligence.getDifficulty()).setTeam(artificialIntelligence.getTeam());
		
		Map<Operation, Float> cloneOperationsWeight = new LinkedHashMap<Operation,Float>(artificialIntelligence.operationsWeight);
		for(Operation o : cloneOperationsWeight.keySet()){
			computeOperation(artificialIntelligence, o);
		}		
	}
	
	private static AIComputer getComputer(Difficulty difficulty){
		switch (difficulty) {
		case EASY:
			return new EasyComputer();
		case MEDIUM:
			return new MediumComputer();
		case HARD:
			return new HardComputer();
		default:
			return new EasyComputer();
		}
	}

	private static void computeOperation(AI artificialIntelligence, Operation o) {
		switch(o.getCard().getClassName()){
		case "saboteur.model.Card.PlanCard":
			operationPlanCard(artificialIntelligence, o);
			break;
		case "saboteur.model.Card.RescueCard":
			operationRescueCard(artificialIntelligence, o);
			break;
		case "saboteur.model.Card.DoubleRescueCard":
			operationDoubleRescueCard(artificialIntelligence, o);
			break;
		case "saboteur.model.Card.SabotageCard":
			operationSabotageCard(artificialIntelligence, o);
			break;
		case "saboteur.model.Card.PathCard":
			operationPathCard(artificialIntelligence, o);
			break;
		case "saboteur.model.Card.CollapseCard" :
			operationCollapseCard(artificialIntelligence, o);
		}
	}

	
	
}
