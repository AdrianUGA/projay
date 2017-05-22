package saboteur.model;

import java.io.Serializable;
import java.util.*;

import saboteur.model.Card.*;

public class Board implements Serializable {

	public static final int IMPOSSIBLE_PATH = 8192;
	private static final long serialVersionUID = -7481864881135990150L;
	private static final int GRID_SIZE = 61;
	private static final int MIDDLE_Y = 30;
	private static final int MIDDLE_X = 30;
	private static final Position START = new Position(MIDDLE_Y,MIDDLE_X);
	public static final int DISTANCE_START_OBJECTIVE_X = 8;
	private static final int DISTANCE_START_OBJECTIVE_Y = 2;
	private static final Position[] goalCardsPositions = new Position[] {
			new Position(getStart().getcX() + DISTANCE_START_OBJECTIVE_X, getStart().getcY()),
			new Position(getStart().getcX() + DISTANCE_START_OBJECTIVE_X, getStart().getcY() + DISTANCE_START_OBJECTIVE_Y),
			new Position(getStart().getcX() + DISTANCE_START_OBJECTIVE_X, getStart().getcY() - DISTANCE_START_OBJECTIVE_Y)};

	private PathCard[][] board;
	private List<Position> objectiveCards;
	private Map<Position, PathCard> pathCardsPosition;
	
	public Board(ArrayList<PathCard> startPathCard, ArrayList<PathCard> goalPathCard){
		this.board = new PathCard[GRID_SIZE][GRID_SIZE];
		for (int i=0; i<GRID_SIZE; i++){
			for (int j=0; j<GRID_SIZE; j++){
				this.board[i][j] = null;
			}
		}
		this.objectiveCards = new LinkedList<Position>();
		this.pathCardsPosition = new LinkedHashMap<Position, PathCard>();
		
		Collections.shuffle(goalPathCard, new Random(Game.seed));
		
		for(int i=0; i<3; i++)
			this.addCard(goalPathCard.get(i), goalCardsPositions[i]);
		this.addCard(startPathCard.get(0), getStart());
		for(Position p : objectiveCards){
			if(this.getCard(p).hasGold()){
				System.out.println("Gold x:" + p.getcX() + " y:" + p.getcY());
			}
		}
	}
	
	public void addCard(PathCard card, Position position){
		if(card == null)
			return;
		if(card.isGoal())
			this.objectiveCards.add(position);
		else
			this.pathCardsPosition.put(position, card);
		
		/* Adding the goal cards when reached */
		this.board[position.getcY()][position.getcX()] = card;
		for(Position p : this.getNeighbors(position)){
			if (this.getCard(p) != null && this.getCard(p).isGoal()){
				ArrayList<Position> goalCardsToFlip = getGoalCardsToFlip(card, position);
				if(goalCardsToFlip.contains(p)){
					this.pathCardsPosition.put(p, this.getCard(position));
					System.out.println("POSITION VOISIN = (" + p.getcX() + "," + p.getcY() + ")");
					System.out.println("CARTE OBJECTIF A RETOURNER");
				}
			}
		}
	}
	
	//Used by AI to test what happens if it put a card somewhere
	//So we don't want goal cards to be shown
	public void temporarAddCard(OperationPathCard o){
		if(o.getCard() == null)
			return;
		if(o.getReversed()){
			this.pathCardsPosition.put(o.getP(),((PathCard) o.getCard()).reversed());
			this.board[o.getP().getcY()][o.getP().getcX()] = ((PathCard) o.getCard()).reversed();
		}else{
			this.pathCardsPosition.put(o.getP(),(PathCard) o.getCard());
			this.board[o.getP().getcY()][o.getP().getcX()] = (PathCard) o.getCard();
		}
	}

	public void removeCard(Position position){
		if(this.getCard(position).isGoal())
			this.objectiveCards.remove(position);
		else
			this.pathCardsPosition.remove(position);
		this.board[position.getcY()][position.getcX()] = null;
	}
	
	//Used by AI
	public PathCard temporarRemoveCard(Position position){
		PathCard removed = this.pathCardsPosition.remove(position);
		this.board[position.getcY()][position.getcX()] = null;
		return removed;
	}
	
	


/* Tests. No side effects */
	
	public boolean isGoalCardWithGoldVisible() {
		PathCard card;
		for (Position posCard : this.objectiveCards){
			card = getCard(posCard);
			if (card.hasGold() && card.isVisible()) return true;
		}
		return false;
	}
	
	/*Return true if there is at least one neighbor coming at the position, and if there is at least one empty neighbor*/
	private boolean canPutAPathCardThere(Position pos) {
		int amountOfComingNeighbor = 0;
		int amountOfAvailableNeighbor = 0;
		for(Cardinal cardinal : Cardinal.values()){
			if(getCard(pos.getNeighbor(cardinal)) == null){
				amountOfAvailableNeighbor ++;
			}else if(getCard(pos.getNeighbor(cardinal)).isOpen(cardinal.opposite())){
				amountOfComingNeighbor++;
			}
		}
		return (amountOfAvailableNeighbor>=1 && amountOfComingNeighbor>=1);
	}
	
	//A bit different from the previous method, return true if it's possible, even if not interesting
	private boolean canPutAnyPathCardThere(Position pos) {
		int amountOfComingNeighbor = 0;
		int amountOfAvailableNeighbor = 0;
		for(Cardinal cardinal : Cardinal.values()){
			if(getCard(pos.getNeighbor(cardinal)) == null){
				amountOfAvailableNeighbor ++;
			}else if(getCard(pos.getNeighbor(cardinal)).isOpen(cardinal.opposite())){
				amountOfComingNeighbor++;
			}
		}
		return ((amountOfAvailableNeighbor>=1 && amountOfComingNeighbor>=1) || amountOfComingNeighbor >=2);
	}

	

	public boolean isPossible(PathCard card, Position position){
		if(!position.isValid())
			return false;
		
		PathCard neighbor;
		boolean atLeastOnePath = false;

		for(Cardinal cardinal : Cardinal.values()){
			neighbor = this.getCard(position.getNeighbor(cardinal));
			
			/* Important test if neighbor is visible because it can be a goalCard */
			if(neighbor == null || !neighbor.isVisible())
				continue;
			
			if (card.isOpen(cardinal)^neighbor.isOpen(cardinal.opposite())) return false;
			if (card.isOpen(cardinal) && neighbor.isOpen(cardinal.opposite())) atLeastOnePath = true;

		}
		
		return (card.isGoal() || card.isStart() || atLeastOnePath);
	}

/* Getters Setters Modifiers */
	
	public Set<Position> extractPositions(Set<OperationPathCard> operations){
		Set<Position> positions = new HashSet<Position>();
		for(OperationPathCard operation : operations){
			positions.add(operation.getP());
		}
		return positions;
	}
	
	public PathCard getCard(Position position){
		if (!position.isValid())
			return null;
		return this.board[position.getcY()][position.getcX()];
	}
	

	
	public int numberOfNeighbors(Position position){
		return this.getNeighbors(position).size();
	}
	
	public List<Position> getNeighbors(Position position){
		LinkedList<Position> positions = new LinkedList<Position>();
		for(Position p : this.getAllNeighbors(position)){
			if (this.getCard(p) != null)
				positions.add(p);
		}
		return positions;
	}
	
	/* Returns all valid positions next to position. */
	public List<Position> getAllNeighbors(Position position){
		LinkedList<Position> positions = new LinkedList<Position>();
		for(Cardinal cardinal : Cardinal.values()){
			Position p = position.getNeighbor(cardinal);
			if (p != null)
				positions.add(p);
		}
		return positions;
	}
	
	public List<Position> getAllEmptyNeighbors(Position position){
		LinkedList<Position> positions = new LinkedList<Position>();
		for(Cardinal cardinal : Cardinal.values()){
			Position p = position.getNeighbor(cardinal);
			if (p == null)
				positions.add(p);
		}
		return positions;
	}	
	
	public Position getPosition(PathCard card){
		if (card == null)
			return null;
		for(Position position : this.pathCardsPosition.keySet()){
			if(this.pathCardsPosition.get(position).equals(card))
				return position;
		}
		
		for(Position goalCard : this.objectiveCards){
			if(this.getCard(goalCard).equals(card))
				return goalCard;
		}
		return null;
	}
	
	public List<Position> getGoalCards(){
		return this.objectiveCards;
	}
	
	/* Returns actions on every free positions when card=null */
	public Set<OperationPathCard> getPossibleOperationPathCard(Player ai, PathCard card){
		Set<OperationPathCard> possiblePlaces = new HashSet<OperationPathCard>();
		
		for(PathCard pathCard : this.pathCardsPosition.values()){
			for(Position neighbor : this.getAllNeighbors(this.getPosition(pathCard))){
				OperationPathCard operation = new OperationPathCard(ai, card, neighbor);
				OperationPathCard operationReversed = new OperationPathCard(ai, card, neighbor).setReversed(true);
				
				if (this.getCard(neighbor) != null)
					continue;
				
				if(card == null){
					if(canPutAPathCardThere(neighbor)){
						possiblePlaces.add(operation);
						possiblePlaces.add(operationReversed);
					}
				}else if(this.isPossible(card, neighbor)){
					possiblePlaces.add(operation);
				}else if(this.isPossible(card.reversed(), neighbor)){
					possiblePlaces.add(operationReversed);
				}
			}
		}
		return possiblePlaces;
	}
	
	/* Returns every free positions for a PathCard*/
	public List<Position> getPossiblePositionPathCard(PathCard card){
		List<Position> possiblePlaces = new LinkedList<Position>();
		
		for(PathCard pathCard : this.pathCardsPosition.values()){
			for(Position neighbor : this.getAllNeighbors(this.getPosition(pathCard))){
				
				if (this.getCard(neighbor) != null)
					continue;
				
				if(card == null){
					System.err.println("Carte null ! (getPossiblePositionPathCard)");
				}else if(this.isPossible(card, neighbor)){
					possiblePlaces.add(neighbor);
				}else if(this.isPossible(card.reversed(), neighbor)){
					possiblePlaces.add(neighbor);
				}
			}
		}
		return possiblePlaces;
	}
	
	public List<Position> getNearestPossiblePathCardPlace(Position position){
		List<Position> possible =  new ArrayList<Position>();
		for(OperationPathCard o : this.getPossibleOperationPathCard(null,null)){
			possible.add(o.getP());
		}
		
		possible.sort(new PositionComparator(position));
		
		int min = position.getTaxiDistance(possible.get(possible.size()-1));
		List<Position> ret = new LinkedList<Position>();
		for(int i=0; i<possible.size(); i++){
			if(possible.get(i).getTaxiDistance(position) > min){
				break;
			}
			ret.add(possible.get(i));
		}
		return ret;
	}
	
	public ArrayList<Position> getGoalCardsToFlip(PathCard card, Position p){
		PathCard neighbor;
		Position posNeighbor;
		ArrayList<Position> result = new ArrayList<>();
		
		ArrayList<Position> positionsToExplore = new ArrayList<>();
		
		PathCard currentCard;
		Position currentPosition;
		
		ArrayList<Integer> positionsAlreadyExplored = new ArrayList<>();
		
		currentPosition = getStart();
		positionsToExplore.add(currentPosition);
		positionsAlreadyExplored.add(getIndice(currentPosition));
		
		while (!positionsToExplore.isEmpty()){
			currentPosition = positionsToExplore.remove(positionsToExplore.size()-1);
			currentCard = this.getCard(currentPosition);
			for(Cardinal cardinal : Cardinal.values()){
				posNeighbor = p.getNeighbor(cardinal);
				neighbor = this.getCard(posNeighbor);
				if (neighbor != null && currentCard.isOpen(cardinal) && neighbor.isOpen(cardinal.opposite())){
					if (!positionsAlreadyExplored.contains(getIndice(posNeighbor))){
						positionsAlreadyExplored.add(getIndice(posNeighbor));
						positionsToExplore.add(posNeighbor);
						if (neighbor.isGoal() && !neighbor.isVisible()){
							result.add(currentPosition);
						}
					}
				}
			}
		}
		for(Cardinal cardinal : Cardinal.values()){
			if (card.isOpen(cardinal)){
				posNeighbor = p.getNeighbor(cardinal);
				neighbor = this.getCard(posNeighbor);
				if (neighbor != null && neighbor.isGoal() && !neighbor.isVisible()){
					result.add(posNeighbor);
				}
			}
		}
		
		return result;
	}
	
	public static Position getStart() {
		return START;
	}

	public int getIndice(Position pos){
		return pos.getcY() * 60 + pos.getcX();
	}
	
	public List<Position> getAllCulDeSac(){
		List<Position> list = new LinkedList<Position>();
		for(Position position : this.pathCardsPosition.keySet()){
			if(this.pathCardsPosition.get(position).isCulDeSac())
				list.add(position);
		}
		return list;
	}

	public static int getGridSize() {
		return GRID_SIZE;
	}

	public Map<Position, PathCard> getPathCardsPosition() {
		return this.pathCardsPosition;
	}
	
	public ArrayList<Position> getAllPlacablePositionFromStart(){
		ArrayList<Position> positionsToExplore = new ArrayList<Position>();
		ArrayList<Position> positionsExplored = new ArrayList<Position>();
		ArrayList<Position> allPlacablePositionFromStart = new ArrayList<Position>();
		
		positionsToExplore.add(Board.START);
		
		while(!positionsToExplore.isEmpty()){
			Position currentPos = positionsToExplore.remove(0);
			positionsExplored.add(currentPos);
			for(Cardinal cardinal : Cardinal.values()){
				Position currentNeighbor = currentPos.getNeighbor(cardinal);
				//Position is null if it's outside from the board
				if(currentNeighbor != null){
					if(this.getCard(currentNeighbor) == null){
						if(canPutAnyPathCardThere(currentNeighbor) && this.getCard(currentPos).isOpen(cardinal)){
							allPlacablePositionFromStart.add(currentNeighbor);
						}
					}else{
						if(!positionsExplored.contains(currentNeighbor) && !this.getCard(currentNeighbor).isCulDeSac()
							  && this.getCard(currentNeighbor).isOpen(cardinal.opposite())){
							positionsToExplore.add(currentNeighbor);
						}
					}
				}
			}
		}
		return allPlacablePositionFromStart;
	}
	
	private ArrayList<Position> getAllEmptyReachablePositions(){
		ArrayList<Position> allEmptyReachablePositions = new ArrayList<Position>();
		for(Position p : this.pathCardsPosition.keySet()){
			if(!getCard(p).isGoal()){
				for(Cardinal cardinal : getCard(p).getOpenSides()){
					if(getCard(p.getNeighbor(cardinal)) == null && canPutAnyPathCardThere(p.getNeighbor(cardinal))){
						allEmptyReachablePositions.add(p.getNeighbor(cardinal));
					}
				}
			}
		}
		return allEmptyReachablePositions;
	}
	
	public int minFromAnyEmptyPositionToGoldCard(Position estimatedGoldCardPosition){ // = min2
		ArrayList<Position> availablePositions = getAllEmptyReachablePositions();
		int distanceToTravelFromAPosition;
		int min = IMPOSSIBLE_PATH;
		
		for(Position currentPos : availablePositions){
			distanceToTravelFromAPosition = aStarOnEmptyCard(currentPos, estimatedGoldCardPosition);
			if(distanceToTravelFromAPosition != -1 && distanceToTravelFromAPosition < min){
				min = distanceToTravelFromAPosition;
			}
		}
		
		return min;
	}
	
	public int minFromEmptyReachablePathCardToGoldCard(Position estimatedGoldCardPosition){ // = min1
		ArrayList<Position> availablePositions = getAllPlacablePositionFromStart();
		int distanceToTravelFromAPosition;
		int min = IMPOSSIBLE_PATH;
		
		for(Position currentPos : availablePositions){
			distanceToTravelFromAPosition = aStarOnEmptyCard(currentPos, estimatedGoldCardPosition);
			if(distanceToTravelFromAPosition != -1 && distanceToTravelFromAPosition < min){
				min = distanceToTravelFromAPosition;
			}
		}
		
		return min;
	}

	private Position smallestdUhU(ArrayList<Position> list, Position destination) {
		int min = 8192;
		Position posToReturn = null;
		for(Position p : list){
			if(p.getTaxiDistance(destination) < min){
				min = p.getTaxiDistance(destination);
				posToReturn = p;
			}
		}
		return posToReturn;
	}
	
	public int aStarOnEmptyCard(Position currentPos, Position estimatedGoldCardPosition){
		ArrayList<Position> qList = new ArrayList<Position>();
		Integer dU[] = new Integer[GRID_SIZE*GRID_SIZE];
		for(int i=0;i<dU.length;i++){
			dU[i] = 65536;
		}

		qList.add(currentPos);
		dU[currentPos.getcY() * GRID_SIZE + currentPos.getcX()] = 0;
		
		while(!qList.isEmpty()){
			Position u = smallestdUhU(qList, estimatedGoldCardPosition);
			if(u.getcX() == estimatedGoldCardPosition.getcX() && u.getcY() == estimatedGoldCardPosition.getcY()){
				return dU[estimatedGoldCardPosition.getcY() * GRID_SIZE + estimatedGoldCardPosition.getcX()];
			}
			qList.remove(u);
			for(Cardinal c : Cardinal.values()){
				if(u.getNeighbor(c) != null && (this.getCard(u.getNeighbor(c)) == null || this.getCard(u.getNeighbor(c)).isGoal())){
					if(dU[u.getcY() * GRID_SIZE + u.getcX()] + 1 < dU[u.getNeighbor(c).getcY() * GRID_SIZE + u.getNeighbor(c).getcX()]){
						dU[u.getNeighbor(c).getcY() * GRID_SIZE + u.getNeighbor(c).getcX()] = dU[u.getcY() * GRID_SIZE + u.getcX()] + 1;
						qList.add(u.getNeighbor(c));
					}
				}
			}
		}
		
		return -1;
	}

}
