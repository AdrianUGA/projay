package saboteur.model;

import saboteur.model.Card.Cardinal;

public class Position {
	private static final int MAX_COORDINATE = 61;
	private int cX;
	private int cY;
	
	public Position(int x, int y){
		cX = x;
		cY = y;
	}
	
	public int getcX() {
		return cX;
	}

	public int getcY() {
		return cY;
	}
	
	public Position greaterX(Position p){
		if(p.cX > this.cX)
			return p;
		return this;
	}
	
	public Position greaterY(Position p){
		if(p.cY > this.cY)
			return p;
		return this;
	}
	
	public Position getNeighbor(Position position, Cardinal cardinal){
		if(cardinal == Cardinal.NORTH)
			position = new Position(position.cX, position.cY-1);
		else if(cardinal == Cardinal.EAST)
			position = new Position(position.cX+1, position.cY);
		else if(cardinal == Cardinal.SOUTH)
			position = new Position(position.cX, position.cY+1);
		else if(cardinal == Cardinal.WEST)
			position = new Position(position.cX-1, position.cY);
		
		if(position.isValid())
			return position;
		return null;
	}
	
	public boolean isValid(){
		if(this.getcX() < 0 || this.getcX() > MAX_COORDINATE-1 || this.getcY() < 0 || this.getcY() > MAX_COORDINATE-1){
			return false;
		}
		return true;
	}
	
	@Override
	public boolean equals(Object o){
		Position p = (Position) o;
		return this.cX == p.cX && this.cY == p.cY;
	}

}
