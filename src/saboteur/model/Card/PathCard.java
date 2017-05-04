package saboteur.model.Card;

public enum PathCard {
	OPENUP(1), OPENRIGHT(2), OPENDOWN(4), OPENLEFT(8), CUL_DE_SAC(16);
	
	int open;
	
	PathCard(int i){
		open = i;
	};
}
