package saboteur.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

public abstract class SaboteurAI {
	
	public static void computeOperationWeightEasyAI(AI artificialIntelligence) {
		Map<Operation, Float> cloneOperationsWeight = new HashMap<Operation,Float>(artificialIntelligence.operationsWeight);
		for(Operation o : cloneOperationsWeight.keySet()){
			switch(o.getCard().getClassName()){
			case "saboteur.model.Card.PlanCard":
				if(!artificialIntelligence.knowsTheGoldCardPosition()){
					((OperationActionCardToBoard) o).setDestinationCard(artificialIntelligence.getGame().getBoard().getCard(artificialIntelligence.getEstimatedGoldCardPosition()));
					artificialIntelligence.operationsWeight.put(o, (float) ((1 + artificialIntelligence.positiveOrZero(Coefficients.SABOTEUR_PLAN_TURN_EASY - artificialIntelligence.getGame().getTurn()))
											* Coefficients.SABOTEUR_PLAN_EASY));
				}
				else{
					// Trash
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -2f);
				}
				break;
			case "saboteur.model.Card.RescueCard":
				if(artificialIntelligence.canRescue((RescueCard)o.getCard())){
					((OperationActionCardToPlayer) o).setDestinationPlayer(artificialIntelligence);
					artificialIntelligence.operationsWeight.put(o, (float) ((4 - artificialIntelligence.getHandicaps().size())*Coefficients.SABOTEUR_HANDICAP_SIZE_EASY) * Coefficients.SABOTEUR_RESCUE_EASY);
				}else{
					// Trash
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
				}
				break;
			case "saboteur.model.Card.DoubleRescueCard":
				if(artificialIntelligence.canRescueWithDoubleRescueCard((DoubleRescueCard)o.getCard())){
					((OperationActionCardToPlayer) o).setDestinationPlayer(artificialIntelligence);
					artificialIntelligence.operationsWeight.put(o, (float) ((4 - artificialIntelligence.getHandicaps().size())*Coefficients.SABOTEUR_HANDICAP_SIZE_EASY) * Coefficients.SABOTEUR_DOUBLERESCUE_EASY);
				}else{
					// Trash
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
				}
				break;
			case "saboteur.model.Card.SabotageCard":
				Player p = artificialIntelligence.mostLikelyADwarf();
				((OperationActionCardToPlayer) o).setDestinationPlayer(p);
				artificialIntelligence.operationsWeight.put(o, (float) (artificialIntelligence.positiveOrZero(artificialIntelligence.AVERAGE_TRUST - artificialIntelligence.isDwarf.get(p)) * Coefficients.SABOTEUR_SABOTAGE_EASY) * ((3-p.getHandicaps().size())/3));
				break;
			case "saboteur.model.Card.PathCard":
				if(artificialIntelligence.getHandicaps().size() == 0){
					// Trash
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
				}
				else if(!((PathCard) o.getCard()).isCulDeSac()){

					Position goldCardPosition = artificialIntelligence.getEstimatedGoldCardPosition();
					List<Position> allClosestPosition = artificialIntelligence.getGame().getBoard().getNearestPossiblePathCardPlace(goldCardPosition);
					Set<OperationPathCard> allOperationsForThisCard = artificialIntelligence.getGame().getBoard().getPossibleOperationPathCard((PathCard) o.getCard());

					int distanceMin = allClosestPosition.get(0).getTaxiDistance(goldCardPosition);
					for(OperationPathCard currentOp : allOperationsForThisCard){
						Position currentPos = artificialIntelligence.getGame().getBoard().getPosition((PathCard)currentOp.getCard());
						int distanceDifference = distanceMin - currentPos.getTaxiDistance(goldCardPosition);
						if(distanceDifference >= -1){
							// At most 1 position away from the minimum
							// If there is < 2 distance left, it put 0 as weight.
							artificialIntelligence.operationsWeight.put(currentOp, 
									(float) artificialIntelligence.ifNegativeZeroElseOne(currentPos.getTaxiDistance(goldCardPosition)-Coefficients.SABOTEUR_DISTANCE_LEFT_EASY)
									*(Coefficients.SABOTEUR_DISTANCE_PATHCARD_EASY + distanceDifference - ((PathCard) currentOp.getCard()).openSidesAmount()/5) * Coefficients.SABOTEUR_PATHCARD_EASY);

							
						}else{
							// Trash
							artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
						}
					}
				}else {
					// Easy Saboteur AI starts playing cul-de-sac when there is only 2 "distance" left

					Position goldCardPosition = artificialIntelligence.getEstimatedGoldCardPosition();
					List<Position> allClosestPosition = artificialIntelligence.getGame().getBoard().getNearestPossibleOperationPathCard(goldCardPosition);
					Set<Position> allPositionsForThisCard = artificialIntelligence.getGame().getBoard().getPossibleOperationPathCard((PathCard) o.getCard());

					int distanceMin = allClosestPosition.get(0).getTaxiDistance(goldCardPosition);
					for(Position currentPos : allPositionsForThisCard){
						int distance = distanceMin - currentPos.getTaxiDistance(goldCardPosition);
						if(distance >= -1){
							// At most 1 position away from the minimum
							((OperationPathCard) o).setP(currentPos);
							artificialIntelligence.operationsWeight.put((OperationPathCard) o, (float) artificialIntelligence.ifNegativeZeroElseOne(Coefficients.SABOTEUR_DISTANCE_LEFT_EASY-currentPos.getTaxiDistance(goldCardPosition))*(Coefficients.SABOTEUR_DISTANCE_PATHCARD_EASY + distance - ((PathCard) o.getCard()).openSidesAmount()/5) * Coefficients.SABOTEUR_CUL_DE_SAC_EASY);
						}else{
							// Trash
							artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
						}
					}
				}
				break;
			case "saboteur.model.Card.CollapseCard" :
				//TODO to be changed (Saboteur are bad guys, right ?)
				List<Position> allCulDeSac = artificialIntelligence.getGame().getBoard().allCulDeSac();
				if(allCulDeSac.size() == 0){
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
				}
				else{
					Random r = new Random(artificialIntelligence.getGame().getSeed());
					Position randomPos = allCulDeSac.get(r.nextInt(allCulDeSac.size()));
					((OperationActionCardToBoard) o).setDestinationCard(artificialIntelligence.getGame().getBoard().getCard(randomPos));
					artificialIntelligence.operationsWeight.put((OperationActionCardToBoard) o, (float) Coefficients.DWARF_COLLAPSE_EASY);
				}
			}
		}
		
	}

	public static void computeOperationWeightMediumAI(AI artificialIntelligence) {
		// TODO Auto-generated method stub
		
	}
	
	public static void computeOperationWeightHardAI(AI artificialIntelligence) {
		// TODO Auto-generated method stub
		
	}
	
}
