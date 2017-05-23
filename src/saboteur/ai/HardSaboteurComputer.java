package saboteur.ai;

import java.util.LinkedList;

import saboteur.model.Board;
import saboteur.model.Operation;
import saboteur.model.OperationActionCardToBoard;
import saboteur.model.OperationActionCardToPlayer;
import saboteur.model.OperationTrash;
import saboteur.model.Player;
import saboteur.model.Position;
import saboteur.model.Card.CollapseCard;
import saboteur.model.Card.RescueCard;
import saboteur.model.Card.SabotageCard;

public class HardSaboteurComputer extends Computer {
	
	private static final int COLLAPSE_AND_CREATE_HOLE = 90;
	private static final int DISTANCE_TO_GOAL_FOR_COLLAPSE = 4;
	private static final float SABOTAGE = 2;
	private static final float DOUBLE_RESCUE = 14;
	public static int PLAN = 75;
	public static int RESCUE_ITSELF = 20;
	public static float HANDICAP_SIZE = 0.5f;
	public static int RESCUE = 15;
	public static int COLLAPSE = 60;

	@Override
	void operationCollapseCard(Operation o) {
		boolean atLeastOne = false;
		Board board = this.artificialIntelligence.getGame().getBoard();
		if(board.minFromAnyEmptyPositionToGoldCard(this.artificialIntelligence.getEstimatedGoldCardPosition()) < DISTANCE_TO_GOAL_FOR_COLLAPSE){
			int min1 = board.minFromEmptyReachablePathCardToGoldCard(artificialIntelligence.getEstimatedGoldCardPosition());
			int min2 = board.minFromAnyEmptyPositionToGoldCard(artificialIntelligence.getEstimatedGoldCardPosition());
			
			for(Position p : this.artificialIntelligence.getGame().getBoard().getPathCardsPosition().keySet()){
				if(board.getCard(p).isGoal() || board.getCard(p).isStart())
					continue;
				if(min1 == min2){
					board.temporarRemoveCard(p);
					int newMin1 = board.minFromEmptyReachablePathCardToGoldCard(artificialIntelligence.getEstimatedGoldCardPosition());
					int newMin2 = board.minFromAnyEmptyPositionToGoldCard(artificialIntelligence.getEstimatedGoldCardPosition());
					
					if(newMin1 != newMin2){
			//TODO			
//						(OperationActionCardToBoard) o.set
//						this.artificialIntelligence.operationsWeight.put(, COLLAPSE_AND_CREATE_HOLE + board.numberOfNeighbors(p));
					}
				}
//				this.artificialIntelligence.operationsWeight.put(o, COLLAPSE + );
			}
		}
		if(!atLeastOne){
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -20f);
		}
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
