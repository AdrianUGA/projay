package saboteur.model.Card;

public enum Cardinal {
	NORTH(1), EAST(2), SOUTH(4), WEST(8);

	private int value;

	private Cardinal(int value){
		this.value = value;
	}
	
	public Cardinal opposite() {
		if(this == Cardinal.NORTH)
			return Cardinal.SOUTH;
		
		if(this == Cardinal.EAST)
			return Cardinal.WEST;
		
		if(this == Cardinal.SOUTH)
			return Cardinal.NORTH;
		
		if(this == Cardinal.WEST)
			return Cardinal.EAST;
		System.err.println("Invalid Cardinal. That is NOT supposed to happen, like ever");
		return null;
	}
	
	public int getValue(){
		return this.value;
	}
}
