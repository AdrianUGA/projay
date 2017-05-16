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
					((OperationActionCardToBoard) o).setDestinationCard(artificialIntelligence.getGame().getBoard().getCard(artificialIntelligence.getEstimatedGoldCardPosition()));
					artificialIntelligence.operationsWeight.put(o, (float) ((1 + artificialIntelligence.positiveOrZero(Coefficients.DWARF_PLAN_TURN_EASY - artificialIntelligence.getGame().getTurn()))
											* Coefficients.DWARF_PLAN_EASY));
				}
				else{
					// Trash
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -2f);
				}
				break;
			case "saboteur.model.Card.RescueCard":
				if(artificialIntelligence.canRescue((RescueCard)o.getCard())){
					((OperationActionCardToPlayer) o).setDestinationPlayer(artificialIntelligence);
					artificialIntelligence.operationsWeight.put(o, (float) ((4 - artificialIntelligence.getHandicaps().size())*Coefficients.DWARF_HANDICAP_SIZE_EASY) * Coefficients.DWARF_RESCUE_EASY);
				}else{
					// Trash
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
				}
				break;
			case "saboteur.model.Card.DoubleRescueCard":
				if(artificialIntelligence.canRescueWithDoubleRescueCard((DoubleRescueCard)o.getCard())){
					((OperationActionCardToPlayer) o).setDestinationPlayer(artificialIntelligence);
					artificialIntelligence.operationsWeight.put(o, (float) ((4 - artificialIntelligence.getHandicaps().size())*Coefficients.DWARF_HANDICAP_SIZE_EASY) * Coefficients.DWARF_DOUBLERESCUE_EASY);
				}else{
					// Trash
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
				}
				break;
			case "saboteur.model.Card.SabotageCard":
				Player p = artificialIntelligence.mostLikelyASaboteur();
				((OperationActionCardToPlayer) o).setDestinationPlayer(p);
				//System.out.println("Nb joueur = " + getGame().getPlayerList().size());
				//System.out.println("Size isDwarf = " + isDwarf.size());
				artificialIntelligence.operationsWeight.put(o, (float) (artificialIntelligence.positiveOrZero(artificialIntelligence.AVERAGE_TRUST - artificialIntelligence.isDwarf.get(p)) * Coefficients.DWARF_SABOTAGE_EASY) * ((3-p.getHandicaps().size())/3));
				break;
			case "saboteur.model.Card.PathCard":
				// R�cup�rer la case la plus proche � vol d'oiseau sur laquelle on peut mettre une carte (= presque dans tous les cas la meilleure case)
				
				if(!((PathCard) o.getCard()).isCulDeSac() && artificialIntelligence.getHandicaps().size() == 0){
					Position goldCardPosition = artificialIntelligence.getEstimatedGoldCardPosition();
					List<Position> allClosestPosition = artificialIntelligence.getGame().getBoard().getNearestPossibleOperationPathCard(goldCardPosition);
					Set<Position> allPositionsForThisCard = artificialIntelligence.getGame().getBoard().getPossibleOperationPathCard((PathCard) o.getCard());
					System.out.println("Pour la carte " + o.getCard());

					int distanceMin = allClosestPosition.get(0).getTaxiDistance(goldCardPosition);
					//System.out.println("closest position x= " + allClosestPosition.get(0).getcX() + " y= " + allClosestPosition.get(0).getcY());
					for(Position currentPos : allPositionsForThisCard){
						System.out.println("Position : x = " + currentPos.getcX() + " y = " + currentPos.getcY());
						int distanceDifference = distanceMin - currentPos.getTaxiDistance(goldCardPosition);
						if(distanceDifference >= -1){
							// At most 1 position away from the minimum
							((OperationPathCard) o).setP(currentPos);
							artificialIntelligence.operationsWeight.put((OperationPathCard) o, (float) (Coefficients.DWARF_DISTANCE_PATHCARD_EASY 
									+ distanceDifference - ((PathCard) o.getCard()).openSidesAmount()/5) * Coefficients.DWARF_PATHCARD_EASY);
						}else{
							// Trash
							artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
						}
					}
				}else{
					// We don't want to play cul-de-sac card, and we can't play a pathcard if our tools are broken
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -1f);
				}
				break;
			case "saboteur.model.Card.CollapseCard" :
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
	
	public static void computeOperationWeightMediumAI(AI ai) {
		// TODO Auto-generated method stub
		
	}
	
	public static void computeOperationWeightHardAI(AI ai) {
		// TODO Auto-generated method stub
		
	}

}
