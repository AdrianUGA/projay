package saboteur.ai;

import saboteur.model.Operation;

public class EasyDwarfComputer extends AIComputer {
	
	public static int SABOTAGE = 1;
	public static float HANDICAP_SIZE = 0.5f;
	public static int RESCUE = 15;
	public static int DOUBLERESCUE = 9;
	public static int PLAN_TURN = 6;
	public static int PLAN = 10;
	public static int PATHCARD = 10;
	public static int DISTANCE_PATHCARD = 2;
	public static int COLLAPSE = 5;
	public static int BETTER_DISTANCE_MIN = 20;
	public static int LIMIT_ESTIMATED_SABOTEUR = 35;

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
