package saboteur.model;

import java.util.ArrayList;

import saboteur.model.Card.*;
import sun.security.x509.IssuingDistributionPointExtension;

public class OperationPathCard extends Operation {
	private Position p;
	
	public Position getP() {
		return p;
	}

	public OperationPathCard(Player sourcePlayer, Card card, Position position) {
		super(sourcePlayer, card);
		this.p = position;
	}

	@Override
	public void exec(Game game) {
		this.getSourcePlayer().removeHandCard(this.getCard());
		game.getBoard().addCard((PathCard)this.getCard(), p);
		
		ArrayList<Position> goalCardsToFlip = game.getBoard().getGoalCardsToFlip((PathCard)this.getCard(), p);
		PathCard toFlip;
		for (Position p : goalCardsToFlip){
			toFlip = game.getBoard().getCard(p);
			
			if (toFlip.isGoal()){
				toFlip.setVisible(true);

				if (!toFlip.hasGold() && !game.getBoard().isPossible(toFlip, p)){
					game.getBoard().removeCard(p);
					game.getBoard().addCard(toFlip.reversed(), p);
				}
			}
		}
	}

	public OperationPathCard setP(Position p) {
		this.p = p;
		return this;
	}
	
	
}
