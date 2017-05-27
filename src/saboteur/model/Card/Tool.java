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

	public static Tool intToTool(int intOfTool) {
		Tool tool = null;
		switch(intOfTool) {
			case 0 :
				tool = Tool.PICKAXE;
				break;
			case 1 :
				tool = Tool.LANTERN;
				break;
			case 2 :
				tool = Tool.CART;
				break;
		}
		return tool;
	}
}
