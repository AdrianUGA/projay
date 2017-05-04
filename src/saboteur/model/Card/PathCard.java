package saboteur.model.Card;

import java.util.BitSet;

public class PathCard extends Card {
	//OPENUP(1), OPENRIGHT(2), OPENDOWN(4), OPENLEFT(8));
	
	final boolean [] openSides;
	final boolean isCulDeSac;
	
	PathCard(int i){
		this.openSides = new boolean[4];
		if(i>=16){
			isCulDeSac = true;
			i=i-16;
		}else{
			isCulDeSac = false;
		}
		if(i>=8){
			openSides[3] = true;
			i=i-8;
		}
		else{
			openSides[3] = false;
		}
		if(i>=4){
			openSides[2] = true;
			i=i-4;
		}
		else{
			openSides[2] = false;
		}
		if(i>=2){
			openSides[1] = false;
			i=i-2;
		}
		else{
			openSides[1] = false;
		}
		if(i>=1){
			openSides[0] = false;
		}
		else{
			openSides[0] = false;
		}
	};
	
	public boolean isCulDeSac(){
		return (this.isCulDeSac);
	}
}
