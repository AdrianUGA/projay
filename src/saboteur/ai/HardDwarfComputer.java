package saboteur.ai;

import saboteur.model.Operation;

public class HardDwarfComputer extends AIComputer {

	public static int MINIMUM_TRUST_DWARF_HARD = 65;
	public static int MINIMUM_TRUST_SABOTEUR_HARD = 35;

	public static int RESCUE_ITSELF = 30;
	public static float HANDICAP_SIZE = 0.5f;
	public static int RESCUE = 20;
	public static int DOUBLERESCUE = 19;
	public static int PLAN = 150;
	public static int COLLAPSE_CAN_REPLACE = 40;
	public static int COLLAPSE_CDS = 25;
	public static int PATHCARD = 40;
	public static float PATHCARD_OPENSIDES = 0.5f;
	public static int PATHCARD_FIXHOLE = 80;
	
	
	@Override
	void operationCollapseCard(Operation o) {
		// TODO Auto-generated method stub

	}

	@Override
	void operationPathCard(Operation o) {
		// TODO Auto-generated method stub

	}

	@Override
	void operationSabotageCard(Operation o) {
		// TODO Auto-generated method stub

	}

	@Override
	void operationDoubleRescueCard(Operation o) {
		// TODO Auto-generated method stub

	}

	@Override
	void operationRescueCard(Operation o) {
		// TODO Auto-generated method stub

	}

	@Override
	void operationPlanCard(Operation o) {
		// TODO Auto-generated method stub

	}

}
