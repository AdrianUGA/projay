package saboteur.model;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
	private Map<Position, Position> childrensDad;
	private List<Position> pathCardsPosition;
	
	public Board(){
		this.board = new PathCard[GRID_SIZE][GRID_SIZE];
		for (int i=0; i<GRID_SIZE; i++){
			for (int j=0; j<GRID_SIZE; j++){
				this.board[i][j] = null;
			}
		}
		this.objectiveCards = new LinkedList<Position>();
		objectiveCards.add(new Position(START.getcX() + DISTANCE_START_OBJECTIVE_X, START.getcY()));
		objectiveCards.add(new Position(START.getcX() + DISTANCE_START_OBJECTIVE_X, START.getcY() + DISTANCE_START_OBJECTIVE_Y));
		objectiveCards.add(new Position(START.getcX() + DISTANCE_START_OBJECTIVE_X, START.getcY() - DISTANCE_START_OBJECTIVE_Y));
		childrensDad.put(START, START);
		pathCardsPosition.add(START);
	}
	
	public void addCard(PathCard card, Position position){
		if(card.isGoal())
			this.objectiveCards.add(position);
		this.pathCardsPosition.add(position);
		this.childrensDad.put(position, find(position));
		this.board[position.getcY()][position.getcX()] = card;
	}

	public void removeCard(Position position){
		this.pathCardsPosition.remove(position);
		//
		childrensDad.clear();
		for(Position current : pathCardsPosition){
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
		while(childrensDad.get(currentPos) != currentPos){
			currentPos = childrensDad.get(currentPos);
		}
		return currentPos;
	}
	
	private boolean areConnected(Position pos1, Position pos2){
		return find(childrensDad.get(pos1)).equals(find(childrensDad.get(pos2)));
	}
	
	private void connect(Position pos1, Position pos2){
		if(!areConnected(pos1, pos2)){
			if(indice(pos1)<indice(pos2)){
				childrensDad.put(find(pos1), find(pos2));
			}
			else{
				childrensDad.put(find(pos2), find(pos1));
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
			Position p = position.get(position, cardinal);
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
	
	public List<Position> getPossiblePathCardPlace(){
		List<Position> possiblePlaces = new LinkedList<Position>();
		
		for(PathCard pathCard : this.getAllCards()){
			for(Position neighbor : this.getAllNeighbors(this.getPositionCard(pathCard))){
				if(this.isPossible(pathCard, neighbor)){
					possiblePlaces.add(neighbor);
				}
			}
		}
		
		return possiblePlaces;
	}
	
	
	
	private List<PathCard> getAllCards() {
		List<PathCard> cards = new LinkedList<PathCard>();
		for (int i=0; i<GRID_SIZE; i++){
			for (int j=0; j<GRID_SIZE; j++){
				if(this.board[i][j] != null){
					cards.add(this.board[i][j]);
				}
			}
		}
		return cards;
	}

	public boolean isPossible(PathCard card, Position position){
		PathCard neighbor;
    	
		//Test North
		neighbor = this.getCard(new Position(position.getcX(), position.getcY()-1));
		if (neighbor != null && (card.isOpen(Cardinal.North)^neighbor.isOpen(Cardinal.South))) return false;
		
		//Test East
		neighbor = this.getCard(new Position(position.getcX()+1, position.getcY()));
		if (neighbor != null && (card.isOpen(Cardinal.East)^neighbor.isOpen(Cardinal.West))) return false;
		
		//Test South
		neighbor = this.getCard(new Position(position.getcX(), position.getcY()+1));
		if (neighbor != null && (card.isOpen(Cardinal.South)^neighbor.isOpen(Cardinal.North))) return false;
		
		//Test West
		neighbor = this.getCard(new Position(position.getcX()-1, position.getcY()));
		if (neighbor != null && (card.isOpen(Cardinal.West)^neighbor.isOpen(Cardinal.East))) return false;
		
		return true;
	}
	
	public int indice(Position pos){
		return pos.getcY() * 60 + pos.getcX();
	}
}
