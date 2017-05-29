package saboteur.ai;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import saboteur.model.Board;
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

public class HardSaboteurComputer extends Computer {
	
	private static final long serialVersionUID = -2838550033321889406L;
	
	private static final float TRASH_PATHCARD = -1f;
	private static final float TRASH_CUL_DE_SAC = -50f;
	private static final float CUL_DE_SAC_WHEN_AGGRESSIVE = 100f;
	private static final float PATHCARD_WHEN_AGGRESSIVE = 50f;
	private static final float PATHCARD_WHEN_PASSIVE = 10f;
	private static final float TRASH_COLLAPSE_CARD = -20;
	private static final float COLLAPSE_AND_CREATE_HOLE = 90;
	private static final float DISTANCE_TO_GOAL_FOR_COLLAPSE = 4;
	private static final float SABOTAGE = 2;
	private static final float DOUBLE_RESCUE = 14;
	private static final int PLAN = 75;
	private static final int RESCUE_ITSELF = 20;
	private static final float HANDICAP_SIZE = 0.5f;
	private static final int RESCUE = 15;
	private static final float COLLAPSE = 60;

	@Override
	void operationCollapseCard(Operation o) {
		boolean atLeastOne = false;
		Board board = this.artificialIntelligence.getGame().getBoard();
		if(board.minFromAnyEmptyPositionToGoldCard(this.artificialIntelligence.getEstimatedGoldCardPosition()) < DISTANCE_TO_GOAL_FOR_COLLAPSE){
			int min1 = board.minFromEmptyReachablePathCardToGoldCard(artificialIntelligence.getEstimatedGoldCardPosition());
			int min2 = board.minFromAnyEmptyPositionToGoldCard(artificialIntelligence.getEstimatedGoldCardPosition());
			LinkedHashSet<Position> allPos = new LinkedHashSet<Position>(this.artificialIntelligence.getGame().getBoard().getPathCardsPosition().keySet());
			
			for(Position p : allPos){
				if(board.getCard(p).isGoal() || board.getCard(p).isStart()){
					continue;
				}
				
				PathCard removed = board.temporarRemoveCard(p);
				
				OperationActionCardToBoard operation = (OperationActionCardToBoard) o;
				operation.setPositionDestination(p);
				operation.setDestinationCard(board.getCard(p));
				
				int newMin1 = board.minFromEmptyReachablePathCardToGoldCard(artificialIntelligence.getEstimatedGoldCardPosition());
				int newMin2 = board.minFromAnyEmptyPositionToGoldCard(artificialIntelligence.getEstimatedGoldCardPosition());
				
				board.temporarAddCard(new OperationPathCard(artificialIntelligence, removed, p));
				
				if(min1 == min2){ /* No hole atm */
					if(newMin1 != newMin2 && min2 != 0){
						atLeastOne = true;
						this.artificialIntelligence.operationsWeight.put(operation, (COLLAPSE_AND_CREATE_HOLE + board.numberOfNeighbors(p))/min2);
					}
				}else{ /* Already an hole */
					if(newMin2 > min2 && min2 != 0){
						atLeastOne = true;
						this.artificialIntelligence.operationsWeight.put(operation, (COLLAPSE + board.numberOfNeighbors(p))/min2);
					}
				}
			}
		}
		if(!atLeastOne){
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), TRASH_COLLAPSE_CARD);
		}
	}

	@Override
	void operationPathCard(Operation o) {
		OperationPathCard op = (OperationPathCard)o;
		PathCard card = (PathCard)op.getCard();
		LinkedHashSet<OperationPathCard> allOperationsForThisCard = this.artificialIntelligence.getGame().getBoard().getPossibleOperationPathCard(this.artificialIntelligence, card);
		Board board = this.artificialIntelligence.getGame().getBoard();
		Position estimatedGoldCardPosition = artificialIntelligence.getEstimatedGoldCardPosition();
		int currentMin = board.minFromAnyEmptyPositionToGoldCard(estimatedGoldCardPosition);
		boolean atLeastOne = false;
		
		if(artificialIntelligence.getHandicaps().size() == 0){
			if(this.saboteurPlaysAgressive()){
				if(card.isCulDeSac()){
					for(OperationPathCard currentOp : allOperationsForThisCard){
						
						board.temporarAddCard(currentOp);
						int nextMin = board.minFromAnyEmptyPositionToGoldCard(estimatedGoldCardPosition);
						board.temporarRemoveCard(currentOp.getP());
						if(nextMin > currentMin){
							//CdS is blocking the path
							atLeastOne = true;
							this.artificialIntelligence.operationsWeight.put(currentOp, (CUL_DE_SAC_WHEN_AGGRESSIVE / (card.getOpenSides().size() * 0.5f)) * nextMin - currentMin);
						}
					}
				}
				else{
					for(OperationPathCard currentOp : allOperationsForThisCard){
						int nextMin;
						board.temporarAddCard(currentOp);
						if(board.minFromAnyEmptyPositionToGoldCard(estimatedGoldCardPosition) == board.minFromEmptyReachablePathCardToGoldCard(estimatedGoldCardPosition)){
							nextMin = board.minFromAnyEmptyPositionToGoldCard(estimatedGoldCardPosition);
						}
						else{
							nextMin = board.minFromEmptyReachablePathCardToGoldCard(estimatedGoldCardPosition);
						}
						board.temporarRemoveCard(currentOp.getP());
						if(nextMin > currentMin){
							//PathCard is increasing the minimum path length
							atLeastOne = true;
							this.artificialIntelligence.operationsWeight.put(currentOp, (PATHCARD_WHEN_AGGRESSIVE / (card.getOpenSides().size() * 0.5f)) * nextMin - currentMin);
						}
					}
				}
			}
			else{
				if(!card.isCulDeSac()){
					for(OperationPathCard currentOp : allOperationsForThisCard){
						board.temporarAddCard(currentOp);
						int nextMin = board.minFromAnyEmptyPositionToGoldCard(estimatedGoldCardPosition);
						board.temporarRemoveCard(currentOp.getP());
						
						if(nextMin < currentMin){
							//PathCard is increasing the minimum path length
							atLeastOne = true;
							this.artificialIntelligence.operationsWeight.put(currentOp, (PATHCARD_WHEN_PASSIVE / (card.getOpenSides().size() * 0.5f)) * nextMin - currentMin);
						}
					}
				}
			}
		}
		if(!atLeastOne ){
			if(card.isCulDeSac()){
				artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), TRASH_CUL_DE_SAC);
			}
			else{
				artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), TRASH_PATHCARD);
			}
		}
	}

	@Override
	void operationSabotageCard(Operation o) {
		LinkedList<Player> mostLikelyDwarfPlayers = artificialIntelligence.getAllMostLikelyDwarfPlayersHardAI(false);
	
		boolean atLeastOne = false;
		if(this.saboteurPlaysAgressive()){
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
		if(this.saboteurPlaysAgressive()){

			for(Player p : mostLikelySaboteursPlayers){
				if(artificialIntelligence.canRescueWithDoubleRescueCard((DoubleRescueCard)o.getCard(), p)){
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
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -11);
		}
	}

	@Override
	void operationRescueCard(Operation o) {
		LinkedList<Player> mostLikelySaboteursPlayers = artificialIntelligence.getAllMostLikelySaboteurPlayersHardAI(true);

		boolean atLeastOne = false;
		if(this.saboteurPlaysAgressive()){

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
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -10);
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
