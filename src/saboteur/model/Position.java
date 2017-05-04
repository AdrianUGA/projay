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
}
