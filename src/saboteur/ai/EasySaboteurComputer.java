package saboteur.ai;

import java.util.List;
import java.util.Random;
import java.util.LinkedHashSet;

import saboteur.model.Game;
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

public class EasySaboteurComputer extends Computer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3779949447922189281L;
	public static int SABOTAGE = 1;
	public static float HANDICAP_SIZE = 0.25f;
	public static int LIMIT_ESTIMATED_DWARF = 50;
	public static int RESCUE = 8;
	public static int DOUBLERESCUE = 7;
	public static int PLAN_TURN = 3;
	public static int PLAN = 8;
	public static int PATHCARD = 10;
	public static int CUL_DE_SAC = 20;
	public static int DISTANCE_PATHCARD = 2;
	public static int COLLAPSE = 5;
	public static int DISTANCE_LEFT = 2;
	
	/* Collapse random card */
	@Override
	public void operationCollapseCard(Operation o) {
		List<Position> allCardsToDestroy = artificialIntelligence.getGame().getBoard().getAllCulDeSac();
		if(allCardsToDestroy.size() == 0){ /* Trash */
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
		}
		else{
			Game game = this.artificialIntelligence.getGame();
			Position randomPos = allCardsToDestroy.get(new Random(game.getSeed()).nextInt(allCardsToDestroy.size()));
			((OperationActionCardToBoard) o).setDestinationCard(game.getBoard().getCard(randomPos));
			((OperationActionCardToBoard) o).setPositionDestination(randomPos);
			artificialIntelligence.operationsWeight.put((OperationActionCardToBoard) o, (float) COLLAPSE);
		}
	}

	@Override
	public void operationPathCard(Operation o) {
		if(artificialIntelligence.getHandicaps().size() != 0){ /* Trash */
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
		}
		else if(!((PathCard) o.getCard()).isCulDeSac()){

			Position goldCardPosition = artificialIntelligence.getEstimatedGoldCardPosition();
			List<Position> allClosestPosition = artificialIntelligence.getGame().getBoard().getNearestPossiblePathCardPlace(goldCardPosition);
			LinkedHashSet<OperationPathCard> allOperationsForThisCard = artificialIntelligence.getGame().getBoard().getPossibleOperationPathCard(artificialIntelligence,(PathCard) o.getCard());
			boolean atLeastOne = false;
			
			int distanceMin = allClosestPosition.get(0).getTaxiDistance(goldCardPosition);
			for(OperationPathCard currentOp : allOperationsForThisCard){
				Position currentPos = currentOp.getP();
				int distanceDifference = distanceMin - currentPos.getTaxiDistance(goldCardPosition);
				if(distanceDifference >= -1){
					// At most 1 position away from the minimum
					// If there is < SABOTEUR_DISTANCE_LEFT distance left, it put 0 as weight.
					artificialIntelligence.operationsWeight.put(currentOp, 
							(float) Maths.ifNegativeZeroElseOne(currentPos.getTaxiDistance(goldCardPosition)-DISTANCE_LEFT)
							*(DISTANCE_PATHCARD + distanceDifference - ((PathCard) currentOp.getCard()).openSidesAmount()/5) * PATHCARD);
					atLeastOne = true;
					
				}
			}
			if(!atLeastOne){
				artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) 0);
			}
		}else {
			// Easy Saboteur AI starts playing cul-de-sac when there is only 2 "distance" left

			Position goldCardPosition = artificialIntelligence.getEstimatedGoldCardPosition();
			List<Position> allClosestPosition = artificialIntelligence.getGame().getBoard().getNearestPossiblePathCardPlace(goldCardPosition);
			LinkedHashSet<OperationPathCard> allOperationsForThisCard = artificialIntelligence.getGame().getBoard().getPossibleOperationPathCard(artificialIntelligence,(PathCard) o.getCard());
			boolean atLeastOne = false;
			
			int distanceMin = allClosestPosition.get(0).getTaxiDistance(goldCardPosition);
			for(OperationPathCard currentOp : allOperationsForThisCard){
				Position currentPos = currentOp.getP();
				int distance = distanceMin - currentPos.getTaxiDistance(goldCardPosition);
				if(distance >= -1){
					// At most 1 position away from the minimum
					artificialIntelligence.operationsWeight.put((OperationPathCard) currentOp, 
						(float) Maths.ifNegativeZeroElseOne(DISTANCE_LEFT-currentPos.getTaxiDistance(goldCardPosition))
						*(DISTANCE_PATHCARD + distance - ((PathCard) currentOp.getCard()).openSidesAmount()/5) 
						* CUL_DE_SAC);
					atLeastOne = true;
				}
			}
			if(!atLeastOne){
				artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -5);
			}
		}
	}

	@Override
	public void operationSabotageCard(Operation o) {
		Player p = artificialIntelligence.mostLikelyADwarf();
		if(artificialIntelligence.isDwarf.get(p) >= LIMIT_ESTIMATED_DWARF && artificialIntelligence.canHandicap((SabotageCard)o.getCard(), p) && artificialIntelligence != p){
			System.out.println("Je vais foutre un malus à " + p.getName());
			((OperationActionCardToPlayer) o).setDestinationPlayer(p);
			artificialIntelligence.operationsWeight.put(o, 
				(float) (Maths.positiveOrZero(artificialIntelligence.isDwarf.get(p) - artificialIntelligence.AVERAGE_TRUST) 
				* SABOTAGE) * ((float)(3-p.getHandicaps().size())/3));
		}
		else{
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -20);
		}
	}

	@Override
	public void operationDoubleRescueCard(Operation o) {
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
			artificialIntelligence.operationsWeight.put(o, 
				(float) ((4 - artificialIntelligence.getHandicaps().size())*HANDICAP_SIZE) 
				* DOUBLERESCUE);
		}else{
			// Trash
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
		}
	}

	public void operationRescueCard(Operation o) {
		if(artificialIntelligence.canRescueItself((RescueCard)o.getCard())){
			((OperationActionCardToPlayer) o).setDestinationPlayer(artificialIntelligence);
			((OperationActionCardToPlayer) o).setToolDestination(((RescueCard)o.getCard()).getRescueType());
			artificialIntelligence.operationsWeight.put(o, 
				(float) ((4 - artificialIntelligence.getHandicaps().size())*HANDICAP_SIZE) 
				* RESCUE);
		}else{
			// Trash
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
		}
	}

	@Override
	public void operationPlanCard(Operation o) {
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
			artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -2f);
		}
	}
}
