package saboteur.model.Card;

public enum Tool {
	PICKAXE(0), LANTERN(1), CART(2);
	
	private int value;
	private Tool(int value) {
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
}
