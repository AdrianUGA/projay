package saboteur.model.Card;

public enum PathCard {
	OPENUP(1), OPENRIGHT(2), OPENDOWN(4), OPENLEFT(5);
	
	int open;
	
	PathCard(int i){
		open = i;
	};
}
