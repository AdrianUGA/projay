package saboteur.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import saboteur.model.Game;
import saboteur.model.Operation;
import saboteur.model.OperationActionCardToBoard;
import saboteur.model.OperationActionCardToPlayer;
import saboteur.model.OperationTrash;
import saboteur.model.Player;
import saboteur.model.Position;
import saboteur.model.Card.DoubleRescueCard;
import saboteur.model.Card.PathCard;
import saboteur.model.Card.RescueCard;

public class SaboteurAI extends AI {
	
	public SaboteurAI(Game game, String name){
		super(game, name);
	}

	@Override
	protected void computeOperationWeightEasyAI() {
		Map<Operation, Float> cloneOperationsWeight = new HashMap<Operation,Float>(operationsWeight);
		for(Operation o : cloneOperationsWeight.keySet()){
			switch(o.getCard().getClassName()){
			case "saboteur.model.Card.PlanCard":
				if(!knowsTheGoldCardPosition()){
					((OperationActionCardToBoard) o).setDestinationCard(getGame().getBoard().getCard(getEstimatedGoldCardPosition()));
					operationsWeight.put(o, (float) ((1 + positiveOrZero(Coefficients.SABOTEUR_PLAN_TURN_EASY - getGame().getTurn()))
											* Coefficients.SABOTEUR_PLAN_EASY));
				}
				else{
					// Trash
					operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -2f);
				}
				break;
			case "saboteur.model.Card.RescueCard":
				if(canRescue((RescueCard)o.getCard())){
					((OperationActionCardToPlayer) o).setDestinationPlayer(this);
					operationsWeight.put(o, (float) ((4 - handicaps.size())*Coefficients.SABOTEUR_HANDICAP_SIZE_EASY) * Coefficients.SABOTEUR_RESCUE_EASY);
				}else{
					// Trash
					operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
				}
				break;
			case "saboteur.model.Card.DoubleRescueCard":
				if(canRescueWithDoubleRescueCard((DoubleRescueCard)o.getCard())){
					((OperationActionCardToPlayer) o).setDestinationPlayer(this);
					operationsWeight.put(o, (float) ((4 - handicaps.size())*Coefficients.SABOTEUR_HANDICAP_SIZE_EASY) * Coefficients.SABOTEUR_DOUBLERESCUE_EASY);
				}else{
					// Trash
					operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
				}
				break;
			case "saboteur.model.Card.SabotageCard":
				Player p = mostLikelyADwarf();
				((OperationActionCardToPlayer) o).setDestinationPlayer(p);
				operationsWeight.put(o, (float) (positiveOrZero(AVERAGE_TRUST - isDwarf.get(p)) * Coefficients.SABOTEUR_SABOTAGE_EASY) * ((3-p.getHandicaps().size())/3));
				break;
			case "saboteur.model.Card.PathCard":
				if(this.getHandicaps().size() == 0){
					// Trash
					operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
				}
				else if(!((PathCard) o.getCard()).isCulDeSac()){
					Position goldCardPosition = getEstimatedGoldCardPosition();
					List<Position> allClosestPosition = getGame().getBoard().getNearestPossiblePathCardPlace(goldCardPosition);
					Set<Position> allPositionsForThisCard = getGame().getBoard().getPossiblePathCardPlace((PathCard) o.getCard());
					int distanceMin = allClosestPosition.get(0).getTaxiDistance(goldCardPosition);
					for(Position currentPos : allPositionsForThisCard){
						int distanceDifference = distanceMin - currentPos.getTaxiDistance(goldCardPosition);
						if(distanceDifference >= -1){
							// At most 1 position away from the minimum
							((OperationActionCardToBoard) o).setDestinationCard(getGame().getBoard().getCard(currentPos));
							// If there is < 2 distance left, it put 0 as weight.
							operationsWeight.put((OperationActionCardToBoard) o, (float) ifNegativeZeroElseOne(currentPos.getTaxiDistance(goldCardPosition)-Coefficients.SABOTEUR_DISTANCE_LEFT_EASY)*(Coefficients.SABOTEUR_DISTANCE_PATHCARD_EASY 
									+ distanceDifference - ((PathCard) o.getCard()).openSidesAmount()/5) * Coefficients.SABOTEUR_PATHCARD_EASY);
						}else{
							// Trash
							operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
						}
					}
				}else {
					// Easy Saboteur AI starts playing cul-de-sac when there is only 2 "distance" left
					Position goldCardPosition = getEstimatedGoldCardPosition();
					List<Position> allClosestPosition = getGame().getBoard().getNearestPossiblePathCardPlace(goldCardPosition);
					Set<Position> allPositionsForThisCard = getGame().getBoard().getPossiblePathCardPlace((PathCard) o.getCard());
					int distanceMin = allClosestPosition.get(0).getTaxiDistance(goldCardPosition);
					for(Position currentPos : allPositionsForThisCard){
						int distance = distanceMin - currentPos.getTaxiDistance(goldCardPosition);
						if(distance >= -1){
							// At most 1 position away from the minimum
							((OperationActionCardToBoard) o).setDestinationCard(getGame().getBoard().getCard(currentPos));
							operationsWeight.put((OperationActionCardToBoard) o, (float) ifNegativeZeroElseOne(Coefficients.SABOTEUR_DISTANCE_LEFT_EASY-currentPos.getTaxiDistance(goldCardPosition))*(Coefficients.SABOTEUR_DISTANCE_PATHCARD_EASY + distance - ((PathCard) o.getCard()).openSidesAmount()/5) * Coefficients.SABOTEUR_CUL_DE_SAC_EASY);
						}else{
							// Trash
							operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
						}
					}
				}
				break;
			case "saboteur.model.Card.CollapseCard" :
				//TODO to be changed (Saboteur are bad guys, right ?)
				List<Position> allCulDeSac = getGame().getBoard().allCulDeSac();
				if(allCulDeSac.size() == 0){
					operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
				}
				else{
					Random r = new Random(getGame().getSeed());
					Position randomPos = allCulDeSac.get(r.nextInt(allCulDeSac.size()));
					((OperationActionCardToBoard) o).setDestinationCard(getGame().getBoard().getCard(randomPos));
					operationsWeight.put((OperationActionCardToBoard) o, (float) Coefficients.DWARF_COLLAPSE_EASY);
				}
			}
		}
		
	}

	@Override
	protected void computeOperationWeightMediumAI() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void computeOperationWeightHardAI() {
		// TODO Auto-generated method stub
		
	}
	
}
