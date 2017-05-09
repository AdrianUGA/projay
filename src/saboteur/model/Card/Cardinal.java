package saboteur.model.Card;

public enum Cardinal {
	North(1), East(2), South(4), West(8);

	private int value;

	private Cardinal(int value){
		this.value = value;
	}
	
	public Cardinal opposite() {
		if(this == Cardinal.North)
			return Cardinal.South;
		
		if(this == Cardinal.East)
			return Cardinal.West;
		
		if(this == Cardinal.South)
			return Cardinal.North;
		
		if(this == Cardinal.West)
			return Cardinal.East;
		System.err.println("Invalid Cardinal. That is NOT supposed to happen, like ever");
		return null;
	}
	
	public int getValue(){
		return this.value;
	}
}
