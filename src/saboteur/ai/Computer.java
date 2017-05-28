package saboteur.ai;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import saboteur.model.Operation;

public abstract class Computer implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2891478424L;
	public static int MINIMUM_TRUST_DWARF_HARD = 65;
	public static int MINIMUM_TRUST_SABOTEUR_HARD = 35;

	
	protected AI artificialIntelligence;
	
	public void compute() {
		if(this.artificialIntelligence == null)
			System.err.println("artificialIntelligence wasn’t set in this computer");
		
		Map<Operation, Float> cloneOperationsWeight = new LinkedHashMap<Operation,Float>(artificialIntelligence.operationsWeight);
		for(Operation o : cloneOperationsWeight.keySet()){
			this.computeOperation(o);
		}		
	}

	private void computeOperation(Operation o) {
		if(o.getCard().isPlanCard()){ /* PLAN CARD */
			this.operationPlanCard(o);	
		}else if(o.getCard().isRescueCard()){ /* RESCUE CARD */
			this.operationRescueCard(o);	
		}else if(o.getCard().isDoubleRescueCard()){ /* DOUBLE RESCUE */
			this.operationDoubleRescueCard(o);	
		}else if(o.getCard().isSabotageCard()){ /* SABOTAGE */
			this.operationSabotageCard(o);	
		}else if(o.getCard().isCollapseCard()){ /* COLLAPSE */
			this.operationCollapseCard(o);
		}else if(o.getCard().isPathCard()){ /* PATHCARD */
			this.operationPathCard(o);	
		}else{
			System.err.println("Not supposed to happen");
		}
	}
	
	public Computer setArtificialIntelligence(AI artificialIntelligence2) {
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
