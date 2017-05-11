package saboteur.ai;

import java.util.List;
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
	
	public DwarfAI(Game game){
		super(game);
	}
	
	@Override
	public void playCard(){
		this.getGame().playOperation(selectOperation());
	}
	
	public Operation selectOperation(){
		resetProbabilitiesToPlayEachOperation();
		switch(this.getDifficulty()){
		case EASY:
			computeOperationWeightEasyAI();
			break;
		case MEDIUM:
			computeOperationWeightMediumAI();
			break;
		case HARD:
			computeOperationWeightHardAI();
			break;
		}
		removeOperationWithNullTarget();
		
		return bestOperationToPlay();
	}
	
	private void computeOperationWeightEasyAI() {
		for(Operation o : operationsWeight.keySet()){
			switch(o.getCard().getClassName()){
			case "PlanCard":
				if(!knowsTheGoldCardPosition()){
					((OperationActionCardToBoard) o).setDestinationCard(getGame().getBoard().getCard(getEstimatedGoldCardPosition()));
					operationsWeight.put(o, (float) ((1 + positiveOrZero(Coefficients.DWARF_PLAN_TURN_EASY - getGame().getTurn()))
											* Coefficients.DWARF_PLAN_EASY));
				}
				else{
					// Trash
					operationsWeight.put((OperationTrash) o, -2f);
				}
				break;
			case "RescueCard":
				if(canRescue((RescueCard)o.getCard())){
					((OperationActionCardToPlayer) o).setDestinationPlayer(this);
					operationsWeight.put(o, (float) ((4 - handicaps.size())*Coefficients.DWARF_HANDICAP_SIZE_EASY) * Coefficients.DWARF_RESCUE_EASY);
				}else{
					// Trash
					operationsWeight.put((OperationTrash) o, 0f);
				}
				break;
			case "DoubleRescueCard":
				if(canRescueWithDoubleRescueCard((DoubleRescueCard)o.getCard())){
					((OperationActionCardToPlayer) o).setDestinationPlayer(this);
					operationsWeight.put(o, (float) ((4 - handicaps.size())*Coefficients.DWARF_HANDICAP_SIZE_EASY) * Coefficients.DWARF_DOUBLERESCUE_EASY);
				}else{
					// Trash
					operationsWeight.put((OperationTrash) o, 0f);
				}
				break;
			case "SabotageCard":
				Player p = mostLikelyASaboteur();
				((OperationActionCardToPlayer) o).setDestinationPlayer(p);
				operationsWeight.put(o, (float) (positiveOrZero(AVERAGE_TRUST - isDwarf.get(p)) * Coefficients.DWARF_SABOTAGE_EASY) * ((3-p.getHandicaps().size())/3));
				break;
			case "PathCard":
				// Récupérer la case la plus proche à vol d'oiseau sur laquelle on peut mettre une carte (= presque dans tous les cas la meilleure case)
				if(!((PathCard) o.getCard()).isCulDeSac()){
					Position goldCardPosition = getEstimatedGoldCardPosition();
					List<Position> allClosestPosition = getGame().getBoard().getNearestPossiblePathCardPlace(goldCardPosition);
					List<Position> allPositionsForThisCard = getGame().getBoard().getPossiblePathCardPlace((PathCard) o.getCard());
					int distanceMin = allClosestPosition.get(0).getTaxiDistance(goldCardPosition);
					for(Position currentPos : allPositionsForThisCard){
						int distance = distanceMin - currentPos.getTaxiDistance(goldCardPosition);
						if(distance >= -1){
							// At most 1 position away from the minimum
							((OperationActionCardToBoard) o).setDestinationCard(getGame().getBoard().getCard(currentPos));
							operationsWeight.put((OperationActionCardToBoard) o, (float) (Coefficients.DWARF_DISTANCE_PATHCARD_EASY 
									+ distance - ((PathCard) o.getCard()).openSidesAmount()/5) * Coefficients.DWARF_PATHCARD_EASY);
						}else{
							// Trash
							operationsWeight.put((OperationTrash) o, 0f);
						}
					}
				}else{
					// We don't want to play cul-de-sac card
					operationsWeight.put((OperationTrash) o, -1f);
				}
				break;
			case "CollapseCard" :
				List<Position> allCulDeSac = getGame().getBoard().allCulDeSac();
				if(allCulDeSac.size() == 0){
					operationsWeight.put((OperationTrash) o, 0f);
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
	
	private void computeOperationWeightMediumAI() {
		// TODO Auto-generated method stub
		
	}

	private void computeOperationWeightHardAI() {
		// TODO Auto-generated method stub
		
	}

}
