package saboteur.ai;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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

public class HardDwarfComputer extends Computer {

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
		//Check for every PathCard already on the board
		//If AI removes it, does it decrease the distance to the gold card ?
		//If yes, (can the AI put a card here ?) OR  (is the card a cul-de-sac ?)
		Map<Position, PathCard> pathCardCopy = new LinkedHashMap<Position, PathCard>(artificialIntelligence.getGame().getBoard().getPathCardsPosition());
		Position goldCardPosition = artificialIntelligence.getEstimatedGoldCardPosition();
		List<Position> allClosestPosition = artificialIntelligence.getGame().getBoard().getNearestPossiblePathCardPlace(goldCardPosition);
		int minimumDistanceBeforeCollapsing = allClosestPosition.get(0).getTaxiDistance(goldCardPosition);
		boolean atLeastOne = false;
		
		for(Position currentPosition : pathCardCopy.keySet()){
			
			if(!artificialIntelligence.getGame().getBoard().getPathCardsPosition().get(currentPosition).isStart() 
			   && !artificialIntelligence.getGame().getBoard().getPathCardsPosition().get(currentPosition).isGoal()){
				
				PathCard removedCard = artificialIntelligence.getGame().getBoard().temporarRemoveCard(currentPosition);
				
				allClosestPosition = artificialIntelligence.getGame().getBoard().getNearestPossiblePathCardPlace(goldCardPosition);
				if(allClosestPosition.get(0).getTaxiDistance(goldCardPosition) < minimumDistanceBeforeCollapsing){
					if(removedCard.isCulDeSac()){
						if(artificialIntelligence.canPlayThere(currentPosition)){
							artificialIntelligence.getGame().getBoard().temporarAddCard(new OperationPathCard(artificialIntelligence, removedCard, currentPosition));
							((OperationActionCardToBoard) o).setDestinationCard(artificialIntelligence.getGame().getBoard().getCard(currentPosition));
							((OperationActionCardToBoard) o).setPositionDestination(currentPosition);
							artificialIntelligence.operationsWeight.put((OperationActionCardToBoard) o, (float) COLLAPSE_CAN_REPLACE + COLLAPSE_CDS);
							atLeastOne = true;
						}
						else{
							artificialIntelligence.getGame().getBoard().temporarAddCard(new OperationPathCard(artificialIntelligence, removedCard, currentPosition));
							((OperationActionCardToBoard) o).setDestinationCard(artificialIntelligence.getGame().getBoard().getCard(currentPosition));
							((OperationActionCardToBoard) o).setPositionDestination(currentPosition);
							artificialIntelligence.operationsWeight.put((OperationActionCardToBoard) o, (float) COLLAPSE_CDS);
							atLeastOne = true;
						}
					}
					else if(artificialIntelligence.canPlayThere(currentPosition) && allClosestPosition.get(0).getTaxiDistance(goldCardPosition) < 2){
						artificialIntelligence.getGame().getBoard().temporarAddCard(new OperationPathCard(artificialIntelligence, removedCard, currentPosition));
						((OperationActionCardToBoard) o).setDestinationCard(artificialIntelligence.getGame().getBoard().getCard(currentPosition));
						((OperationActionCardToBoard) o).setPositionDestination(currentPosition);
						artificialIntelligence.operationsWeight.put((OperationActionCardToBoard) o, (float) COLLAPSE_CAN_REPLACE);
						atLeastOne = true;
					}
				}
				else{
					artificialIntelligence.getGame().getBoard().temporarAddCard(new OperationPathCard(artificialIntelligence, removedCard, currentPosition));
				}
				
			}
		}
		if(!atLeastOne){
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
		}

	}

	@Override
	void operationPathCard(Operation o) {
		if(!((PathCard) o.getCard()).isCulDeSac() && artificialIntelligence.getHandicaps().size() == 0){
			Position goldCardPosition = artificialIntelligence.getEstimatedGoldCardPosition();
			List<Position> allClosestPosition = artificialIntelligence.getGame().getBoard().getNearestPossiblePathCardPlace(goldCardPosition);
			Set<OperationPathCard> allOperationsForThisCard = artificialIntelligence.getGame().getBoard().getPossibleOperationPathCard(artificialIntelligence,(PathCard) o.getCard());

			int distanceMin = allClosestPosition.get(0).getTaxiDistance(goldCardPosition);
			for(OperationPathCard currentOp : allOperationsForThisCard){
				Position currentPos = currentOp.getP();
				int distanceDifference = distanceMin - currentPos.getTaxiDistance(goldCardPosition);
				if(distanceDifference >= -1){
					// At most 1 position away from the minimum
					
					//Checking new minimum taxiDistance if AI put the card
					artificialIntelligence.getGame().getBoard().temporarAddCard(currentOp);		
					allClosestPosition = artificialIntelligence.getGame().getBoard().getNearestPossiblePathCardPlace(goldCardPosition);
					int newDistanceMin = allClosestPosition.get(0).getTaxiDistance(goldCardPosition);
					artificialIntelligence.getGame().getBoard().removeCard(currentOp.getP());
					if(distanceMin - newDistanceMin ==1){
						artificialIntelligence.operationsWeight.put(currentOp, 
								(float) ((EasyDwarfComputer.DISTANCE_PATHCARD + distanceDifference 
								+ ((PathCard) currentOp.getCard()).openSidesAmount()/5f) * PATHCARD) + EasyDwarfComputer.BETTER_DISTANCE_MIN);
					}else{
						artificialIntelligence.operationsWeight.put(currentOp, 
								(float) (EasyDwarfComputer.DISTANCE_PATHCARD + distanceDifference 
								- ((PathCard) currentOp.getCard()).openSidesAmount()/5) * PATHCARD);
					}
					
				}else{
					// Trash
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
				}
			}
		}else{
			// We don't want to play cul-de-sac card, and we can't play a pathcard if our tools are broken
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -35f);
		}

	}

	@Override
	void operationSabotageCard(Operation o) {
		LinkedList<Player> mostLikelySaboteurPlayers = artificialIntelligence.getAllMostLikelySaboteurPlayersHardAI(false);
		if(mostLikelySaboteurPlayers.size() == 0 || (mostLikelySaboteurPlayers.size() == 1 && mostLikelySaboteurPlayers.get(0) == artificialIntelligence)){
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -10);
		}
		else{
			boolean atLeastOne = false;
			for(Player p : mostLikelySaboteurPlayers){
				//AI won't hurt itself... it isn't masochistic... Or is it ?
				if(p != artificialIntelligence && artificialIntelligence.canHandicap((SabotageCard)o.getCard(), p)){
					((OperationActionCardToPlayer) o).setDestinationPlayer(p);
					atLeastOne = true;
					artificialIntelligence.operationsWeight.put(o, (float) (Maths.positiveOrZero(artificialIntelligence.AVERAGE_TRUST - artificialIntelligence.isDwarf.get(p)) * EasyDwarfComputer.SABOTAGE) * ((3-p.getHandicaps().size())/3));
				}
			}
			if(!atLeastOne){
				artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -10);
			}
		}
	}

	@Override
	void operationDoubleRescueCard(Operation o) {
		LinkedList<Player> mostLikelyDwarfPlayers = artificialIntelligence.getAllMostLikelyDwarfPlayersHardAI(true);
		if(mostLikelyDwarfPlayers.size() == 0){
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -19);
		}
		else{
			boolean atLeastOne = false;
			for(Player p : mostLikelyDwarfPlayers){
				if(artificialIntelligence.canRescueWithDoubleRescueCard((DoubleRescueCard)o.getCard(), p)){
					((OperationActionCardToPlayer) o).setDestinationPlayer(p);
					//Set tool
					if(artificialIntelligence.canRescueType(((DoubleRescueCard)o.getCard()).getRescueType1()) && artificialIntelligence.canRescueType(((DoubleRescueCard)o.getCard()).getRescueType2())){
						((OperationActionCardToPlayer) o).setToolDestination(((DoubleRescueCard)o.getCard()).getOneOfTheTwoType());
					}
					else if(artificialIntelligence.canRescueType(((DoubleRescueCard)o.getCard()).getRescueType1())){
						((OperationActionCardToPlayer) o).setToolDestination(((DoubleRescueCard)o.getCard()).getRescueType1());
					}
					else{
						((OperationActionCardToPlayer) o).setToolDestination(((DoubleRescueCard)o.getCard()).getRescueType2());
					}
					if(p == artificialIntelligence){
						//Rescue itself
						atLeastOne = true;
						artificialIntelligence.operationsWeight.put(o, (float) ((4 - artificialIntelligence.getHandicaps().size())*HANDICAP_SIZE) * DOUBLERESCUE + RESCUE_ITSELF);
					}else{
						//Rescue ally
						atLeastOne = true;
						artificialIntelligence.operationsWeight.put(o, (float) ((4 - p.getHandicaps().size())*HANDICAP_SIZE) * DOUBLERESCUE + (artificialIntelligence.getIsDwarf().get(p) - artificialIntelligence.AVERAGE_TRUST) );
					}
				}
			}
			if(!atLeastOne){
				artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -19);
			}
		}
	}

	@Override
	void operationRescueCard(Operation o) {
		LinkedList<Player> mostLikelyDwarfPlayers = artificialIntelligence.getAllMostLikelyDwarfPlayersHardAI(true);
		if(mostLikelyDwarfPlayers.size() == 0){
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -20);
		}
		else{
			boolean atLeastOne = false;
			for(Player p : mostLikelyDwarfPlayers){
				if(artificialIntelligence.canRescue((RescueCard)o.getCard(), p)){
					((OperationActionCardToPlayer) o).setDestinationPlayer(p);
					if(p == artificialIntelligence){
						//Rescue itself
						artificialIntelligence.operationsWeight.put(o, (float) ((4 - artificialIntelligence.getHandicaps().size())*HANDICAP_SIZE) * RESCUE + RESCUE_ITSELF);
						atLeastOne = true;
					}else{
						//Rescue ally
						artificialIntelligence.operationsWeight.put(o, (float) ((4 - p.getHandicaps().size())*HANDICAP_SIZE) * RESCUE + (artificialIntelligence.getIsDwarf().get(p) - artificialIntelligence.AVERAGE_TRUST) );
						atLeastOne = true;
					}
				}
			}
			if(!atLeastOne){
				artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -20);
			}
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
		else{
			// Trash
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
		}
	}

}
