package saboteur.ai;

import java.util.LinkedHashMap;
import java.util.Map;

import saboteur.model.Operation;

public abstract class AIComputer {
	
	protected AI artificialIntelligence;
	
	public void compute() {
		Map<Operation, Float> cloneOperationsWeight = new LinkedHashMap<Operation,Float>(artificialIntelligence.operationsWeight);
		for(Operation o : cloneOperationsWeight.keySet()){
			this.computeOperation(o);
		}		
	}

	private void computeOperation(Operation o) {
		switch(o.getCard().getClassName()){
		case "saboteur.model.Card.PlanCard":
			this.operationPlanCard(o);
			break;
		case "saboteur.model.Card.RescueCard":
			this.operationRescueCard(o);
			break;
		case "saboteur.model.Card.DoubleRescueCard":
			this.operationDoubleRescueCard(o);
			break;
		case "saboteur.model.Card.SabotageCard":
			this.operationSabotageCard(o);
			break;
		case "saboteur.model.Card.PathCard":
			this.operationPathCard(o);
			break;
		case "saboteur.model.Card.CollapseCard" :
			this.operationCollapseCard(o);
		}
	}
	
	public AIComputer setArtificialIntelligence(AI artificialIntelligence2) {
		this.artificialIntelligence = artificialIntelligence2;
		return this;
	}


/* To override */
	
	abstract void operationCollapseCard(Operation o);
	abstract void operationPathCard(Operation o);
	abstract void operationSabotageCard(Operation o);
	abstract void operationDoubleRescueCard(Operation o);
	abstract void operationRescueCard(Operation o);
	abstract void operationPlanCard(Operation o);
}
