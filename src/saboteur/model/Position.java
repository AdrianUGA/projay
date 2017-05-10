package saboteur.model;

public class Position {
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
	
	@Override
	public boolean equals(Object o){
		Position p = (Position) o;
		return this.cX == p.cX && this.cY == p.cY;
	}

}
