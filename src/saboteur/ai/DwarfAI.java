package saboteur.ai;

import java.util.HashMap;
import java.util.LinkedList;
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
import saboteur.model.Card.PathCard;
import saboteur.model.Card.RescueCard;
import saboteur.model.Card.DoubleRescueCard;

public abstract class DwarfAI {
	
	public static void computeOperationWeightEasyAI(AI artificialIntelligence) {
		Map<Operation, Float> cloneOperationsWeight = new HashMap<Operation,Float>(artificialIntelligence.operationsWeight);
		for(Operation o : cloneOperationsWeight.keySet()){
			switch(o.getCard().getClassName()){
			case "saboteur.model.Card.PlanCard":
				if(!artificialIntelligence.knowsTheGoldCardPosition()){
					Position estimatedGoldCardPosition = artificialIntelligence.getEstimatedGoldCardPosition();
					((OperationActionCardToBoard) o).setDestinationCard(artificialIntelligence.getGame().getBoard().getCard(estimatedGoldCardPosition));
					((OperationActionCardToBoard) o).setPositionDestination(estimatedGoldCardPosition);
					artificialIntelligence.operationsWeight.put(o, 
							(float) ((1 + artificialIntelligence.positiveOrZero(Coefficients.DWARF_PLAN_TURN_EASY 
									- artificialIntelligence.getGame().getTurn())) * Coefficients.DWARF_PLAN_EASY));
				}
				else{
					// Trash
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -40f);
				}
				break;
			case "saboteur.model.Card.RescueCard":
				if(artificialIntelligence.canRescue((RescueCard)o.getCard())){
					((OperationActionCardToPlayer) o).setDestinationPlayer(artificialIntelligence);
					artificialIntelligence.operationsWeight.put(o, (float) ((4 - artificialIntelligence.getHandicaps().size())*Coefficients.DWARF_HANDICAP_SIZE_EASY) * Coefficients.DWARF_RESCUE_EASY);
				}else{
					// Trash
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -10f);
				}
				break;
			case "saboteur.model.Card.DoubleRescueCard":
				if(artificialIntelligence.canRescueWithDoubleRescueCard((DoubleRescueCard)o.getCard())){
					((OperationActionCardToPlayer) o).setDestinationPlayer(artificialIntelligence);
					artificialIntelligence.operationsWeight.put(o, (float) ((4 - artificialIntelligence.getHandicaps().size())*Coefficients.DWARF_HANDICAP_SIZE_EASY) * Coefficients.DWARF_DOUBLERESCUE_EASY);
				}else{
					// Trash
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -5f);
				}
				break;
			case "saboteur.model.Card.SabotageCard":
				Player p = artificialIntelligence.mostLikelyASaboteur();
				if(artificialIntelligence.isDwarf.get(p) <= Coefficients.DWARF_LIMIT_ESTIMATED_SABOTEUR_EASY){
					((OperationActionCardToPlayer) o).setDestinationPlayer(p);
					artificialIntelligence.operationsWeight.put(o, (float) (artificialIntelligence.positiveOrZero(artificialIntelligence.AVERAGE_TRUST - artificialIntelligence.isDwarf.get(p)) * Coefficients.DWARF_SABOTAGE_EASY) * ((3-p.getHandicaps().size())/3));
				}else{
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -20);
				}
				break;
			case "saboteur.model.Card.PathCard":				
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
										(float) ((Coefficients.DWARF_DISTANCE_PATHCARD_EASY + distanceDifference 
										+ ((PathCard) currentOp.getCard()).openSidesAmount()/5f) * Coefficients.DWARF_PATHCARD_EASY) + Coefficients.DWARF_BETTER_DISTANCE_MIN_EASY);
							}else{
								artificialIntelligence.operationsWeight.put(currentOp, 
										(float) (Coefficients.DWARF_DISTANCE_PATHCARD_EASY + distanceDifference 
										- ((PathCard) currentOp.getCard()).openSidesAmount()/5) * Coefficients.DWARF_PATHCARD_EASY);
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
				break;
			case "saboteur.model.Card.CollapseCard" :
				List<Position> allCulDeSac = artificialIntelligence.getGame().getBoard().allCulDeSac();
				if(allCulDeSac.size() == 0){
					//IA only try to destroy cul-de-sac
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
				}
				else{
					Random r = new Random(artificialIntelligence.getGame().getSeed());
					Position randomPos = allCulDeSac.get(r.nextInt(allCulDeSac.size()));
					((OperationActionCardToBoard) o).setDestinationCard(artificialIntelligence.getGame().getBoard().getCard(randomPos));
					((OperationActionCardToBoard) o).setPositionDestination(randomPos);
					artificialIntelligence.operationsWeight.put((OperationActionCardToBoard) o, (float) Coefficients.DWARF_COLLAPSE_EASY);
				}
			}
		}
		
	}
	
	public static void computeOperationWeightMediumAI(AI artificialIntelligence) {
		// TODO Auto-generated method stub
		
	}
	
	public static void computeOperationWeightHardAI(AI artificialIntelligence) {
		Map<Operation, Float> cloneOperationsWeight = new HashMap<Operation,Float>(artificialIntelligence.operationsWeight);
		for(Operation o : cloneOperationsWeight.keySet()){
			if(o.getCard().isPlanCard()){ // PLAN CARD
				if(!artificialIntelligence.knowsTheGoldCardPosition()){
					Position estimatedGoldCardPosition = artificialIntelligence.getEstimatedGoldCardPosition();
					((OperationActionCardToBoard) o).setDestinationCard(artificialIntelligence.getGame().getBoard().getCard(estimatedGoldCardPosition));
					((OperationActionCardToBoard) o).setPositionDestination(estimatedGoldCardPosition);
					artificialIntelligence.operationsWeight.put(o, (float) (Coefficients.DWARF_PLAN_HARD));
				}
				else{
					// Trash
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -50f);
				}
			}
			else if(o.getCard().isRescueCard()){
				LinkedList<Player> mostLikelyDwarfPlayers = artificialIntelligence.getAllMostLikelyDwarfPlayersHardAI();
				for(Player p : mostLikelyDwarfPlayers){
					if(artificialIntelligence.canRescue((RescueCard)o.getCard(), p)){
						((OperationActionCardToPlayer) o).setDestinationPlayer(p);
						if(p == artificialIntelligence){
							//Rescue itself
							artificialIntelligence.operationsWeight.put(o, (float) ((4 - artificialIntelligence.getHandicaps().size())*Coefficients.DWARF_HANDICAP_SIZE_HARD) * Coefficients.DWARF_RESCUE_HARD + Coefficients.DWARF_RESCUE_ITSELF_HARD);
						}else{
							//Rescue ally
							artificialIntelligence.operationsWeight.put(o, (float) ((4 - artificialIntelligence.getHandicaps().size())*Coefficients.DWARF_HANDICAP_SIZE_HARD) * Coefficients.DWARF_RESCUE_HARD + (artificialIntelligence.getIsDwarf().get(p) - artificialIntelligence.AVERAGE_TRUST) );
						}
					}
				}
			}
		}
		
	}

}
