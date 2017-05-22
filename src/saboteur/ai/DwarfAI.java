package saboteur.ai;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import saboteur.model.Board;
import saboteur.model.Operation;
import saboteur.model.OperationActionCardToBoard;
import saboteur.model.OperationActionCardToPlayer;
import saboteur.model.OperationPathCard;
import saboteur.model.OperationTrash;
import saboteur.model.Player;
import saboteur.model.Position;
import saboteur.model.Card.PathCard;
import saboteur.model.Card.RescueCard;
import saboteur.model.Card.SabotageCard;
import saboteur.model.Card.DoubleRescueCard;

public abstract class DwarfAI {
	
	public static void computeOperationWeightEasyAI(AI artificialIntelligence) {
		//Can't use 'for each' on operationsWeight
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
				if(artificialIntelligence.canRescueItself((RescueCard)o.getCard())){
					((OperationActionCardToPlayer) o).setDestinationPlayer(artificialIntelligence);
					((OperationActionCardToPlayer) o).setToolDestination(((RescueCard)o.getCard()).getRescueType());
					artificialIntelligence.operationsWeight.put(o, (float) ((4 - artificialIntelligence.getHandicaps().size())*Coefficients.DWARF_HANDICAP_SIZE_EASY) * Coefficients.DWARF_RESCUE_EASY);
				}else{
					// Trash
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -10f);
				}
				break;
			case "saboteur.model.Card.DoubleRescueCard":
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
					artificialIntelligence.operationsWeight.put(o, (float) ((4 - artificialIntelligence.getHandicaps().size())*Coefficients.DWARF_HANDICAP_SIZE_EASY) * Coefficients.DWARF_DOUBLERESCUE_EASY);
				}else{
					// Trash
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), -5f);
				}
				break;
			case "saboteur.model.Card.SabotageCard":
				Player p = artificialIntelligence.mostLikelyASaboteur();
				if(artificialIntelligence.isDwarf.get(p) <= Coefficients.DWARF_LIMIT_ESTIMATED_SABOTEUR_EASY && artificialIntelligence.canHandicap((SabotageCard)o.getCard(), p)){
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
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
				}
			}
			else if(o.getCard().isRescueCard()){ // RESCUE CARD
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
								artificialIntelligence.operationsWeight.put(o, (float) ((4 - artificialIntelligence.getHandicaps().size())*Coefficients.DWARF_HANDICAP_SIZE_HARD) * Coefficients.DWARF_RESCUE_HARD + Coefficients.DWARF_RESCUE_ITSELF_HARD);
								atLeastOne = true;
							}else{
								//Rescue ally
								artificialIntelligence.operationsWeight.put(o, (float) ((4 - p.getHandicaps().size())*Coefficients.DWARF_HANDICAP_SIZE_HARD) * Coefficients.DWARF_RESCUE_HARD + (artificialIntelligence.getIsDwarf().get(p) - artificialIntelligence.AVERAGE_TRUST) );
								atLeastOne = true;
							}
						}
					}
					if(!atLeastOne){
						artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -20);
					}
				}
			}
			else if(o.getCard().isDoubleRescueCard()){ // DOUBLE RESCUE
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
								artificialIntelligence.operationsWeight.put(o, (float) ((4 - artificialIntelligence.getHandicaps().size())*Coefficients.DWARF_HANDICAP_SIZE_HARD) * Coefficients.DWARF_DOUBLERESCUE_HARD + Coefficients.DWARF_RESCUE_ITSELF_HARD);
							}else{
								//Rescue ally
								atLeastOne = true;
								artificialIntelligence.operationsWeight.put(o, (float) ((4 - p.getHandicaps().size())*Coefficients.DWARF_HANDICAP_SIZE_HARD) * Coefficients.DWARF_DOUBLERESCUE_HARD + (artificialIntelligence.getIsDwarf().get(p) - artificialIntelligence.AVERAGE_TRUST) );
							}
						}
					}
					if(!atLeastOne){
						artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -19);
					}
				}
			}
			else if(o.getCard().isSabotageCard()){ // SABOTAGE
				LinkedList<Player> mostLikelySaboteurPlayers = artificialIntelligence.getAllMostLikelySaboteurPlayersHardAI(false);
				if(mostLikelySaboteurPlayers.size() == 0 || (mostLikelySaboteurPlayers.size() == 1 && mostLikelySaboteurPlayers.get(0) == artificialIntelligence)){
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -10);
				}
				else{
					boolean atLeastOne = false;
					for(Player p : mostLikelySaboteurPlayers){
						//AI won't hurt itself... it isn't masochistic
						if(p != artificialIntelligence && artificialIntelligence.canHandicap((SabotageCard)o.getCard(), p)){
							((OperationActionCardToPlayer) o).setDestinationPlayer(p);
							atLeastOne = true;
							artificialIntelligence.operationsWeight.put(o, (float) (artificialIntelligence.positiveOrZero(artificialIntelligence.AVERAGE_TRUST - artificialIntelligence.isDwarf.get(p)) * Coefficients.DWARF_SABOTAGE_EASY) * ((3-p.getHandicaps().size())/3));
						}
					}
					if(!atLeastOne){
						artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), (float) -10);
					}
				}
			}
			else if(o.getCard().isCollapseCard()){ // COLLAPSE
				//Check for every PathCard already on the board
				//If AI removes it, does it decrease the distance to the gold card ?
				//If yes, (can the AI put a card here ?) OR  (is the card a cul-de-sac ?)
				Map<Position, PathCard> pathCardCopy = new HashMap<Position, PathCard>(artificialIntelligence.getGame().getBoard().getPathCardsPosition());
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
									artificialIntelligence.operationsWeight.put((OperationActionCardToBoard) o, (float) Coefficients.DWARF_COLLAPSE_CAN_REPLACE_HARD + Coefficients.DWARF_COLLAPSE_CDS_HARD);
									atLeastOne = true;
								}
								else{
									artificialIntelligence.getGame().getBoard().temporarAddCard(new OperationPathCard(artificialIntelligence, removedCard, currentPosition));
									((OperationActionCardToBoard) o).setDestinationCard(artificialIntelligence.getGame().getBoard().getCard(currentPosition));
									((OperationActionCardToBoard) o).setPositionDestination(currentPosition);
									artificialIntelligence.operationsWeight.put((OperationActionCardToBoard) o, (float) Coefficients.DWARF_COLLAPSE_CDS_HARD);
									atLeastOne = true;
								}
							}
							else if(artificialIntelligence.canPlayThere(currentPosition) && allClosestPosition.get(0).getTaxiDistance(goldCardPosition) < 2){
								artificialIntelligence.getGame().getBoard().temporarAddCard(new OperationPathCard(artificialIntelligence, removedCard, currentPosition));
								((OperationActionCardToBoard) o).setDestinationCard(artificialIntelligence.getGame().getBoard().getCard(currentPosition));
								((OperationActionCardToBoard) o).setPositionDestination(currentPosition);
								artificialIntelligence.operationsWeight.put((OperationActionCardToBoard) o, (float) Coefficients.DWARF_COLLAPSE_CAN_REPLACE_HARD);
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
			else if(o.getCard().isPathCard()){ // PATHCARD
				System.out.println("PathCard = " + o.getCard() + " " + ((PathCard)o.getCard()).isCulDeSac() + " " + artificialIntelligence.getHandicaps().size());
				if(((PathCard) o.getCard()).isCulDeSac() || !(artificialIntelligence.getHandicaps().size() == 0)){
					artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
				}
				else{
					Position estimatedGoldCardPosition = artificialIntelligence.getEstimatedGoldCardPosition();
					Board board = artificialIntelligence.getGame().getBoard();
					int minimumFromStart = board.minFromEmptyReachablePathCardToGoldCard(estimatedGoldCardPosition); // = min1
					int minimumFromAnywhere = board.minFromAnyEmptyPositionToGoldCard(estimatedGoldCardPosition); // = min2
					boolean atLeastOneOperation = false;
					
					if(minimumFromStart == Board.IMPOSSIBLE_PATH){
						System.out.println("pas possible depuis début");
						if(minimumFromAnywhere == Board.IMPOSSIBLE_PATH){
							System.out.println("pas possible du tout");
							//There is a loop, can't progress
							//Do nothing
						}
						else{
							System.out.println("Loop at start");
							//There is a loop at the start
							//Trying to improve min2
							Set<OperationPathCard> allOperationsForThisCard = board.getPossibleOperationPathCard(artificialIntelligence,(PathCard) o.getCard());
							for(OperationPathCard currentOp : allOperationsForThisCard){
								board.temporarAddCard(currentOp);
								
								int currentMin = board.minFromAnyEmptyPositionToGoldCard(estimatedGoldCardPosition);
								if(currentMin < minimumFromAnywhere){
									artificialIntelligence.operationsWeight.put(currentOp, 
											(float) Coefficients.DWARF_PATHCARD_HARD /  (((PathCard)currentOp.getCard()).openSidesAmount() * Coefficients.DWARF_PATHCARD_OPENSIDES_HARD));
									atLeastOneOperation = true;
								}
								else if(currentMin-1 < minimumFromAnywhere){ //Allow the AI to play if it can't directly improve the path
									//TODO (probleme = tout le temps vrai car le chemin minimum reste le meme)
								}
							
								board.temporarRemoveCard(currentOp.getP());
							}
						}
					}
					else if(minimumFromStart == minimumFromAnywhere){ //There is no hole
						//Trying to improve min2
						System.out.println("No hole");
						Set<OperationPathCard> allOperationsForThisCard = board.getPossibleOperationPathCard(artificialIntelligence,(PathCard) o.getCard());
						for(OperationPathCard currentOp : allOperationsForThisCard){
							
							board.temporarAddCard(currentOp);
							
							int currentMin = board.minFromAnyEmptyPositionToGoldCard(estimatedGoldCardPosition);
							if(currentMin < minimumFromAnywhere){
								artificialIntelligence.operationsWeight.put(currentOp, 
										(float) Coefficients.DWARF_PATHCARD_HARD / (((PathCard)currentOp.getCard()).openSidesAmount() * Coefficients.DWARF_PATHCARD_OPENSIDES_HARD));
								atLeastOneOperation = true;
							}
						
							board.temporarRemoveCard(currentOp.getP());
						}
					}
					else{ // There is a hole
						//Trying to fix the hole
						System.out.println("Hole");
						Set<OperationPathCard> allOperationsForThisCard = board.getPossibleOperationPathCard(artificialIntelligence,(PathCard) o.getCard());
						for(OperationPathCard currentOp : allOperationsForThisCard){
							board.temporarAddCard(currentOp);
							
							int currentMinFromStart = board.minFromEmptyReachablePathCardToGoldCard(estimatedGoldCardPosition);
							int currentMinFromAnywhere = board.minFromAnyEmptyPositionToGoldCard(estimatedGoldCardPosition);
							
							if(currentMinFromStart == currentMinFromAnywhere){
								//Can fix the hole
								artificialIntelligence.operationsWeight.put(currentOp, 
										(float) (Coefficients.DWARF_PATHCARD_FIXHOLE_HARD + Coefficients.DWARF_PATHCARD_HARD) / (((PathCard)currentOp.getCard()).openSidesAmount() * Coefficients.DWARF_PATHCARD_OPENSIDES_HARD));
								atLeastOneOperation = true;
								board.temporarRemoveCard(currentOp.getP());
								break; //There is only 1 possible place to fix the hole
							}
							else if(currentMinFromAnywhere < minimumFromAnywhere){
								//Doesn't fix the hole but could be interesting
								artificialIntelligence.operationsWeight.put(currentOp, 
										(float) Coefficients.DWARF_PATHCARD_HARD / (((PathCard)currentOp.getCard()).openSidesAmount() * Coefficients.DWARF_PATHCARD_OPENSIDES_HARD));
								atLeastOneOperation = true;
							}
							
							board.temporarRemoveCard(currentOp.getP());
						}
					}
					if(!atLeastOneOperation){
						System.out.println("Pas possible");
						artificialIntelligence.operationsWeight.put(new OperationTrash(o.getSourcePlayer(),o.getCard()), 0f);
					}
				}
			}
		}
	}
	
}
