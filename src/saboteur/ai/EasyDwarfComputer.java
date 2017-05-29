package saboteur.ai;

import java.util.List;
import java.util.Random;
import java.util.LinkedHashSet;

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

public class EasyDwarfComputer extends Computer {
	
	private static final long serialVersionUID = -8589886740345244240L;
	
	private static final int SABOTAGE = 1;
	private static final float HANDICAP_SIZE = 0.5f;
	private static final int RESCUE = 15;
	private static final int DOUBLERESCUE = 9;
	private static final int PLAN_TURN = 6;
	private static final int PLAN = 10;
	private static final int PATHCARD = 10;
	private static final int DISTANCE_PATHCARD = 2;
	private static final int COLLAPSE = 5;
	private static final int BETTER_DISTANCE_MIN = 20;
	private static final int LIMIT_ESTIMATED_SABOTEUR = 35;

	@Override
	void operationCollapseCard(Operation o) {
		List<Position> allCulDeSac = artificialIntelligence.getGame().getBoard().getAllCulDeSac();
		if(allCulDeSac.size() == 0){
			//IA only try to destroy cul-de-sac
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
		}
		else{
			Random r = new Random(artificialIntelligence.getGame().getSeed());
			Position randomPos = allCulDeSac.get(r.nextInt(allCulDeSac.size()));
			((OperationActionCardToBoard) o).setDestinationCard(artificialIntelligence.getGame().getBoard().getCard(randomPos));
			((OperationActionCardToBoard) o).setPositionDestination(randomPos);
			artificialIntelligence.operationsWeight.put((OperationActionCardToBoard) o, (float) COLLAPSE);
		}
	}

	@Override
	void operationPathCard(Operation o) {
		System.out.println("/ ! \\ IA FACILE");
		if(!((PathCard) o.getCard()).isCulDeSac() && artificialIntelligence.getHandicaps().size() == 0){
			Position goldCardPosition = artificialIntelligence.getEstimatedGoldCardPosition();
			List<Position> allClosestPosition = artificialIntelligence.getGame().getBoard().getNearestPossiblePathCardPlace(goldCardPosition);
			LinkedHashSet<OperationPathCard> allOperationsForThisCard = artificialIntelligence.getGame().getBoard().getPossibleOperationPathCard(artificialIntelligence,(PathCard) o.getCard());

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
								(float) ((DISTANCE_PATHCARD + distanceDifference 
								+ ((PathCard) currentOp.getCard()).openSidesAmount()/5f) * PATHCARD) + BETTER_DISTANCE_MIN);
					}else{
						artificialIntelligence.operationsWeight.put(currentOp, 
								(float) (DISTANCE_PATHCARD + distanceDifference 
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
		Player p = artificialIntelligence.mostLikelyASaboteur();
		if(artificialIntelligence.isDwarf.get(p) <= LIMIT_ESTIMATED_SABOTEUR && artificialIntelligence.canHandicap((SabotageCard)o.getCard(), p)){
			((OperationActionCardToPlayer) o).setDestinationPlayer(p);
			artificialIntelligence.operationsWeight.put(o, (float) (Maths.positiveOrZero(artificialIntelligence.AVERAGE_TRUST - artificialIntelligence.isDwarf.get(p)) * SABOTAGE) * ((3-p.getHandicaps().size())/3));
		}else{
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -20);
		}
	}

	@Override
	void operationDoubleRescueCard(Operation o) {
		if(artificialIntelligence.canRescueWithDoubleRescueCard((DoubleRescueCard)o.getCard())){
			((OperationActionCardToPlayer) o).setDestinationPlayer(artificialIntelligence);
			if(artificialIntelligence.canRescueType(((DoubleRescueCard)o.getCard()).getRescueType1()) && artificialIntelligence.canRescueType(((DoubleRescueCard)o.getCard()).getRescueType2())){
				((OperationActionCardToPlayer) o).setToolDestination(((DoubleRescueCard)o.getCard()).getOneOfTheTwoType());
			}
			else if(artificialIntelligence.canRescueType(((DoubleRescueCard)o.getCard()).getRescueType1())){
				((OperationActionCardToPlayer) o).setToolDestination(((DoubleRescueCard)o.getCard()).getRescueType1());
			}
			else{
				((OperationActionCardToPlayer) o).setToolDestination(((DoubleRescueCard)o.getCard()).getRescueType2());
			}
			artificialIntelligence.operationsWeight.put(o, (float) ((4 - artificialIntelligence.getHandicaps().size())*HANDICAP_SIZE) * DOUBLERESCUE);
		}else{
			// Trash
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -5f);
		}
	}

	@Override
	void operationRescueCard(Operation o) {
		if(artificialIntelligence.canRescueItself((RescueCard)o.getCard())){
			((OperationActionCardToPlayer) o).setDestinationPlayer(artificialIntelligence);
			((OperationActionCardToPlayer) o).setToolDestination(((RescueCard)o.getCard()).getRescueType());
			artificialIntelligence.operationsWeight.put(o, (float) ((4 - artificialIntelligence.getHandicaps().size())*HANDICAP_SIZE) * RESCUE);
		}else{
			// Trash
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -10f);
		}
	}

	@Override
	void operationPlanCard(Operation o) {
		if(!artificialIntelligence.knowsTheGoldCardPosition()){
			Position estimatedGoldCardPosition = artificialIntelligence.getEstimatedGoldCardPosition();
			((OperationActionCardToBoard) o).setDestinationCard(artificialIntelligence.getGame().getBoard().getCard(estimatedGoldCardPosition));
			((OperationActionCardToBoard) o).setPositionDestination(estimatedGoldCardPosition);
			artificialIntelligence.operationsWeight.put(o, 
					(float) ((1 + Maths.positiveOrZero(PLAN_TURN 
							- artificialIntelligence.getGame().getTurn())) * PLAN));
		}
		else{
			// Trash
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -40f);
		}
	}

}
