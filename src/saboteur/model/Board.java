package saboteur.model;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import saboteur.model.Card.*;

public class Board {
	private static final int GRID_SIZE = 61;
	private static final int MIDDLE_Y = 30;
	private static final int MIDDLE_X = 30;
	private static final Position START = new Position(MIDDLE_Y,MIDDLE_X);
	private static final int DISTANCE_START_OBJECTIVE_X = 7;
	private static final int DISTANCE_START_OBJECTIVE_Y = 2;
	
	private PathCard[][] board;
	private List<Position> objectiveCards;
	
	private Map<Position, Position> childrenDad;
	private Map<Position, PathCard> pathCardsPosition;
	
	public Board(ArrayList<PathCard> startPathCard, ArrayList<PathCard> goalPathCard){
		this.board = new PathCard[GRID_SIZE][GRID_SIZE];
		for (int i=0; i<GRID_SIZE; i++){
			for (int j=0; j<GRID_SIZE; j++){
				this.board[i][j] = null;
			}
		}
		this.objectiveCards = new LinkedList<Position>();
		this.pathCardsPosition = new HashMap<Position, PathCard>();
		
		// TODO add the wining card
		// TODO melange le tableau goalPathCard
		/*int rand = ThreadLocalRandom.current().nextInt(0, 3);
		PathCard[] goals = new PathCard[3];
		for(int i=0; i<3; i++){
			goals[i] = new PathCard(15).setToGoal();
			if (rand == i)
				goals[i].setToGold();
		}*/

		this.addCard(goalPathCard.get(0), new Position(START.getcX() + DISTANCE_START_OBJECTIVE_X, START.getcY()));
		this.addCard(goalPathCard.get(1), new Position(START.getcX() + DISTANCE_START_OBJECTIVE_X, START.getcY() + DISTANCE_START_OBJECTIVE_Y));
		this.addCard(goalPathCard.get(2), new Position(START.getcX() + DISTANCE_START_OBJECTIVE_X, START.getcY() - DISTANCE_START_OBJECTIVE_Y));
		
		this.addCard(startPathCard.get(0), START);
	}
	
	public void addCard(PathCard card, Position position){
		if(card.isGoal())
			this.objectiveCards.add(position);
		this.pathCardsPosition.put(position, card);
		this.childrenDad.put(position, find(position));
		this.board[position.getcY()][position.getcX()] = card;
	}

	public void removeCard(Position position){
		this.pathCardsPosition.remove(position);
		//
		childrenDad.clear();
		for(Position current : pathCardsPosition.keySet()){
			for(Position neighbor : getAllNeighbors(current)){
				if(!areConnected(current,neighbor)){
					connect(current,neighbor);
				}
			}
		}
		//
		this.board[position.getcY()][position.getcX()] = null;
	}
	
	private Position find(Position position) {
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
	}
	
	public PathCard getCard(Position position){
		if (!position.isValid())
			return null;
		return this.board[position.getcY()][position.getcX()];
	}
	
	public int getTaxiDistance(Position p1, Position p2) {
		return Math.abs(p2.getcY() - p1.getcY()) + Math.abs(p2.getcX() - p1.getcX());
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
	
	public Position getPositionCard(PathCard card){
		PathCard current;
		for (int i=0; i<GRID_SIZE; i++){
			for (int j=0; j<GRID_SIZE; j++){
				current = board[i][j];
				if (current != null && current == card){ //TODO ne serait-ce pas mieux avec .equals ?
					return new Position(j, i);
				}
			}
		}
		return new Position(-1, -1);
	}
	
	public List<Position> getGoldCards(){
		return this.objectiveCards;
	}
	
	public List<Position> getPossiblePathCardPlace(PathCard card){
		List<Position> possiblePlaces = new LinkedList<Position>();
		
		for(PathCard pathCard : this.pathCardsPosition.values()){
			for(Position neighbor : this.getAllNeighbors(this.getPositionCard(pathCard))){
				if(this.isPossible(card, neighbor)){
					possiblePlaces.add(neighbor);
				}
			}
		}
		
		return possiblePlaces;
	}
	

	public boolean isPossible(PathCard card, Position position){
		PathCard neighbor;
		boolean atLeastOnePath = false;
    	
		//Test North
		neighbor = this.getCard(new Position(position.getcX(), position.getcY()-1));
		if (neighbor != null){
			if (card.isOpen(Cardinal.NORTH) && neighbor.isOpen(Cardinal.SOUTH)) atLeastOnePath = true;
			if (card.isOpen(Cardinal.NORTH)^neighbor.isOpen(Cardinal.SOUTH)) return false;
		}
		
		//Test East
		neighbor = this.getCard(new Position(position.getcX()+1, position.getcY()));
		if (neighbor != null){
			if (card.isOpen(Cardinal.EAST) && neighbor.isOpen(Cardinal.WEST)) atLeastOnePath = true;
			if (card.isOpen(Cardinal.EAST)^neighbor.isOpen(Cardinal.WEST)) return false;
		}
		
		//Test South
		neighbor = this.getCard(new Position(position.getcX(), position.getcY()+1));
		if (neighbor != null){
			if (card.isOpen(Cardinal.SOUTH) && neighbor.isOpen(Cardinal.NORTH)) atLeastOnePath = true;
			if (card.isOpen(Cardinal.SOUTH)^neighbor.isOpen(Cardinal.NORTH)) return false;
		}
		
		//Test West
		neighbor = this.getCard(new Position(position.getcX()-1, position.getcY()));
		if (neighbor != null){
			if (card.isOpen(Cardinal.WEST) && neighbor.isOpen(Cardinal.EAST)) atLeastOnePath = true;
			if (card.isOpen(Cardinal.WEST)^neighbor.isOpen(Cardinal.EAST)) return false;
		}
		
		return (card.isGoal() || card.isStart() || atLeastOnePath);
	}
	
	public int indice(Position pos){
		return pos.getcY() * 60 + pos.getcX();
	}
}
