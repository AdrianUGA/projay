package saboteur.model;

import java.util.*;

import saboteur.model.Card.*;

public class Board {
	private static final int GRID_SIZE = 61;
	private static final int MIDDLE_Y = 30;
	private static final int MIDDLE_X = 30;
	public static final Position START = new Position(MIDDLE_Y,MIDDLE_X);
	public static final int DISTANCE_START_OBJECTIVE_X = 7;
	private static final int DISTANCE_START_OBJECTIVE_Y = 2;
	
	private PathCard[][] board;
	private List<Position> objectiveCards;
	private Map<Position, PathCard> pathCardsPosition;
	
	//private Map<Position, Position> childrenDad;
	
	public Board(ArrayList<PathCard> startPathCard, ArrayList<PathCard> goalPathCard){
		this.board = new PathCard[GRID_SIZE][GRID_SIZE];
		for (int i=0; i<GRID_SIZE; i++){
			for (int j=0; j<GRID_SIZE; j++){
				this.board[i][j] = null;
			}
		}
		this.objectiveCards = new LinkedList<Position>();
		this.pathCardsPosition = new HashMap<Position, PathCard>();
		
		Collections.shuffle(goalPathCard);
		Position[] goalCardsPositions = new Position[] {
				new Position(START.getcX() + DISTANCE_START_OBJECTIVE_X, START.getcY()),
				new Position(START.getcX() + DISTANCE_START_OBJECTIVE_X, START.getcY() + DISTANCE_START_OBJECTIVE_Y),
				new Position(START.getcX() + DISTANCE_START_OBJECTIVE_X, START.getcY() - DISTANCE_START_OBJECTIVE_Y)};
		
		for(int i=0; i<3; i++)
			this.addCard(goalPathCard.get(i), goalCardsPositions[i]);
		this.addCard(startPathCard.get(0), START);
	}
	
	public void addCard(PathCard card, Position position){
		if(card.isGoal())
			this.objectiveCards.add(position);
		else
			this.pathCardsPosition.put(position, card);
		
		/* Adding the goal cards when reached */
		for(Position p : this.getNeighbors(position)){
			this.pathCardsPosition.put(p, this.getCard(position));
		}
		//this.childrenDad.put(position, find(position));
		this.board[position.getcY()][position.getcX()] = card;
	}

	public void removeCard(Position position){
		if(this.getCard(position).isGoal())
			this.objectiveCards.remove(position);
		else
			this.pathCardsPosition.remove(position);
		/*
		childrenDad.clear();
		for(Position current : pathCardsPosition.keySet()){
			for(Position neighbor : getAllNeighbors(current)){
				if(!areConnected(current,neighbor)){
					connect(current,neighbor);
				}
			}
		}
		*/
		this.board[position.getcY()][position.getcX()] = null;
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
	
	public Position getPosition(PathCard card){
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
	
	/* Returns every free positions when card=null */
	public List<Position> getPossiblePathCardPlace(PathCard card){
		List<Position> possiblePlaces = new LinkedList<Position>();
		
		for(PathCard pathCard : this.pathCardsPosition.values()){
			for(Position neighbor : this.getAllNeighbors(this.getPosition(pathCard))){
				if(card != null && this.isPossible(card, neighbor) || card == null && this.getCard(neighbor) == null){
					possiblePlaces.add(neighbor);
				}
			}
		}
		
		return possiblePlaces;
	}
	
	public List<Position> getNearestPossiblePathCardPlace(Position position){
		List<Position> possible = this.getPossiblePathCardPlace(null);
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
	
	public ArrayList<Position> getGoalCardsToFlip(PathCard card, Position p){
		PathCard neighbor;
		Position posNeighbor;
		ArrayList<Position> result = new ArrayList<>();
		
		ArrayList<Position> positionsToExplore = new ArrayList<>();
		
		PathCard currentCard;
		Position currentPosition;
		
		ArrayList<Integer> positionsAlreadyExplored = new ArrayList<>();
		
		currentPosition = START;
		positionsToExplore.add(currentPosition);
		positionsAlreadyExplored.add(indice(currentPosition));
		
		while (!positionsToExplore.isEmpty()){
			currentPosition = positionsToExplore.remove(positionsToExplore.size()-1);
			currentCard = this.getCard(currentPosition);
			for(Cardinal cardinal : Cardinal.values()){
				posNeighbor = p.getNeighbor(cardinal);
				neighbor = this.getCard(posNeighbor);
				if (neighbor != null && currentCard.isOpen(cardinal) && neighbor.isOpen(cardinal.opposite())){
					if (!positionsAlreadyExplored.contains(indice(posNeighbor))){
						positionsAlreadyExplored.add(indice(posNeighbor));
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
	
	public int indice(Position pos){
		return pos.getcY() * 60 + pos.getcX();
	}
	
	public List<Position> allCulDeSac(){
		//TODO
		List<Position> list = new LinkedList<Position>();
		return list;
	}
	
/* Union find stuff */
	
	/*private Position find(Position position) {
		Position currentPos = position;
		while(childrenDad.get(currentPos) != currentPos){
			currentPos = childrenDad.get(currentPos);
		}
		return currentPos;
	}
	
	private boolean areConnected(Position pos1, Position pos2){
		return find(childrenDad.get(pos1)).equals(find(childrenDad.get(pos2)));
	}
	
	private void connect(Position pos1, Position pos2){
		if(!areConnected(pos1, pos2)){
			if(indice(pos1)<indice(pos2)){
				childrenDad.put(find(pos1), find(pos2));
			}
			else{
				childrenDad.put(find(pos2), find(pos1));
			}
		}
	}*/

	public boolean goalCardWithGoldIsVisible() {
		PathCard card;
		for (Position posCard : this.objectiveCards){
			card = getCard(posCard);
			if (card.hasGold() && card.isVisible()) return true;
		}
		return false;
	}
}
