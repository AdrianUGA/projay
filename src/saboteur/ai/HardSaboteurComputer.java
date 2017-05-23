package saboteur.ai;

import java.util.LinkedList;

import saboteur.model.Operation;
import saboteur.model.OperationActionCardToBoard;
import saboteur.model.OperationActionCardToPlayer;
import saboteur.model.OperationTrash;
import saboteur.model.Player;
import saboteur.model.Position;
import saboteur.model.Card.RescueCard;
import saboteur.model.Card.SabotageCard;

public class HardSaboteurComputer extends Computer {
	
	private static final float SABOTAGE = 2;
	private static final float DOUBLE_RESCUE = 14;
	public static int PLAN = 75;
	public static int RESCUE_ITSELF = 20;
	public static float HANDICAP_SIZE = 0.5f;
	public static int RESCUE = 15;

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
		LinkedList<Player> mostLikelyDwarfPlayers = artificialIntelligence.getAllMostLikelyDwarfPlayersHardAI(false);
		
		boolean atLeastOne = false;
		if(this.artificialIntelligence.getGame().getBoard().minFromAnyEmptyPositionToGoldCard(this.artificialIntelligence.getEstimatedGoldCardPosition()) < 5){
			for(Player p : mostLikelyDwarfPlayers){
				//AI won't hurt itself... it isn't masochistic... Or is it ?
				if(p != artificialIntelligence && artificialIntelligence.canHandicap((SabotageCard)o.getCard(), p)){
					((OperationActionCardToPlayer) o).setDestinationPlayer(p);
					atLeastOne = true;
					artificialIntelligence.operationsWeight.put(o, (float) (Maths.positiveOrZero(artificialIntelligence.AVERAGE_TRUST - artificialIntelligence.isDwarf.get(p)) * SABOTAGE) * ((3-p.getHandicaps().size())/3));
				}
			}
		}
		if(!atLeastOne){ /* Trash */
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -10);
		}
	}

	@Override
	void operationDoubleRescueCard(Operation o) {
		LinkedList<Player> mostLikelySaboteursPlayers = artificialIntelligence.getAllMostLikelySaboteurPlayersHardAI(true);

		boolean atLeastOne = false;
		if(this.artificialIntelligence.getGame().getBoard().minFromAnyEmptyPositionToGoldCard(this.artificialIntelligence.getEstimatedGoldCardPosition()) < 5){

			for(Player p : mostLikelySaboteursPlayers){
				if(artificialIntelligence.canRescue((RescueCard)o.getCard(), p)){
					((OperationActionCardToPlayer) o).setDestinationPlayer(p);
					if (p.getHandicaps().size() != 1)
						continue;
					if(p == artificialIntelligence){ /* Rescue itself */
						artificialIntelligence.operationsWeight.put(o, (float) (3*HANDICAP_SIZE) * DOUBLE_RESCUE + RESCUE_ITSELF);
						atLeastOne = true;
					}else{ /* Rescue ally */
						artificialIntelligence.operationsWeight.put(o, (float) (3*HANDICAP_SIZE) * DOUBLE_RESCUE + (artificialIntelligence.AVERAGE_TRUST - artificialIntelligence.getIsDwarf().get(p)) );
						atLeastOne = true;
					}
				}
			}
		}
		if(!atLeastOne){
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -20);
		}
	}

	@Override
	void operationRescueCard(Operation o) {
		LinkedList<Player> mostLikelySaboteursPlayers = artificialIntelligence.getAllMostLikelySaboteurPlayersHardAI(true);

		boolean atLeastOne = false;
		if(this.artificialIntelligence.getGame().getBoard().minFromAnyEmptyPositionToGoldCard(this.artificialIntelligence.getEstimatedGoldCardPosition()) < 5){

			for(Player p : mostLikelySaboteursPlayers){
				if(artificialIntelligence.canRescue((RescueCard)o.getCard(), p)){
					((OperationActionCardToPlayer) o).setDestinationPlayer(p);
					if (p.getHandicaps().size() != 1)
						continue;
					if(p == artificialIntelligence){ /* Rescue itself */
						artificialIntelligence.operationsWeight.put(o, (float) (3*HANDICAP_SIZE) * RESCUE + RESCUE_ITSELF);
						atLeastOne = true;
					}else{ /* Rescue ally */
						artificialIntelligence.operationsWeight.put(o, (float) (3*HANDICAP_SIZE) * RESCUE + (artificialIntelligence.AVERAGE_TRUST - artificialIntelligence.getIsDwarf().get(p)) );
						atLeastOne = true;
					}
				}
			}
		}
		if(!atLeastOne){
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -20);
		}
	}

	@Override
	void operationPlanCard(Operation o) {
		if(!artificialIntelligence.knowsTheGoldCardPosition()){
			Position estimatedGoldCardPosition = artificialIntelligence.getEstimatedGoldCardPosition();
			((OperationActionCardToBoard) o).setDestinationCard(artificialIntelligence.getGame().getBoard().getCard(estimatedGoldCardPosition));
			((OperationActionCardToBoard) o).setPositionDestination(estimatedGoldCardPosition);
			artificialIntelligence.operationsWeight.put(o, (float) (PLAN));
		}
		else{ /* Trash */
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
		}
	}

}
