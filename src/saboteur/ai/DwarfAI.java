package saboteur.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import saboteur.model.Game;
import saboteur.model.Operation;
import saboteur.model.OperationActionCardToBoard;
import saboteur.model.OperationActionCardToPlayer;
import saboteur.model.OperationTrash;
import saboteur.model.Player;
import saboteur.model.Position;
import saboteur.model.Card.PathCard;
import saboteur.model.Card.RescueCard;
import saboteur.model.Card.DoubleRescueCard;

public class DwarfAI extends AI {
	
	public DwarfAI(Game game, String name){
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
					operationsWeight.put(o, (float) ((1 + positiveOrZero(Coefficients.DWARF_PLAN_TURN_EASY - getGame().getTurn()))
											* Coefficients.DWARF_PLAN_EASY));
				}
				else{
					// Trash
					operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -2f);
				}
				break;
			case "saboteur.model.Card.RescueCard":
				if(canRescue((RescueCard)o.getCard())){
					((OperationActionCardToPlayer) o).setDestinationPlayer(this);
					operationsWeight.put(o, (float) ((4 - handicaps.size())*Coefficients.DWARF_HANDICAP_SIZE_EASY) * Coefficients.DWARF_RESCUE_EASY);
				}else{
					// Trash
					operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
				}
				break;
			case "saboteur.model.Card.DoubleRescueCard":
				if(canRescueWithDoubleRescueCard((DoubleRescueCard)o.getCard())){
					((OperationActionCardToPlayer) o).setDestinationPlayer(this);
					operationsWeight.put(o, (float) ((4 - handicaps.size())*Coefficients.DWARF_HANDICAP_SIZE_EASY) * Coefficients.DWARF_DOUBLERESCUE_EASY);
				}else{
					// Trash
					operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
				}
				break;
			case "saboteur.model.Card.SabotageCard":
				Player p = mostLikelyASaboteur();
				((OperationActionCardToPlayer) o).setDestinationPlayer(p);
				System.out.println("Nb joueur = " + getGame().getPlayerList().size());
				System.out.println("Size isDwarf = " + isDwarf.size());
				operationsWeight.put(o, (float) (positiveOrZero(AVERAGE_TRUST - isDwarf.get(p)) * Coefficients.DWARF_SABOTAGE_EASY) * ((3-p.getHandicaps().size())/3));
				break;
			case "saboteur.model.Card.PathCard":
				// R�cup�rer la case la plus proche � vol d'oiseau sur laquelle on peut mettre une carte (= presque dans tous les cas la meilleure case)
				if(!((PathCard) o.getCard()).isCulDeSac() && this.getHandicaps().size() == 0){
					Position goldCardPosition = getEstimatedGoldCardPosition();
					List<Position> allClosestPosition = getGame().getBoard().getNearestPossiblePathCardPlace(goldCardPosition);
					List<Position> allPositionsForThisCard = getGame().getBoard().getPossiblePathCardPlace((PathCard) o.getCard());
					int distanceMin = allClosestPosition.get(0).getTaxiDistance(goldCardPosition);
					for(Position currentPos : allPositionsForThisCard){
						int distanceDifference = distanceMin - currentPos.getTaxiDistance(goldCardPosition);
						if(distanceDifference >= -1){
							// At most 1 position away from the minimum
							((OperationActionCardToBoard) o).setDestinationCard(getGame().getBoard().getCard(currentPos));
							operationsWeight.put((OperationActionCardToBoard) o, (float) (Coefficients.DWARF_DISTANCE_PATHCARD_EASY 
									+ distanceDifference - ((PathCard) o.getCard()).openSidesAmount()/5) * Coefficients.DWARF_PATHCARD_EASY);
						}else{
							// Trash
							operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
						}
					}
				}else{
					// We don't want to play cul-de-sac card, and we can't play a pathcard if our tools are broken
					operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -1f);
				}
				break;
			case "saboteur.model.Card.CollapseCard" :
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
