package saboteur.model;

import java.util.ArrayList;

import saboteur.model.Card.*;

public class OperationPathCard extends Operation {
	private Position p;
	private ArrayList<Position> goalCardsToFlip;
	
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
		System.out.println("carte posé en x= " + p.getcX() + " y= " +p.getcY());
		
		this.goalCardsToFlip = game.getBoard().getGoalCardsToFlip((PathCard)this.getCard(), p);
		PathCard toFlip;
		for (Position p : this.goalCardsToFlip){
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
	
	@Override
	public void execReverse(Game game) {
		this.getSourcePlayer().addHandCard(this.getCard());
		game.getBoard().removeCard(p);
		
		PathCard toFlip;
		for (Position p : this.goalCardsToFlip){
			toFlip = game.getBoard().getCard(p);
			
			if (toFlip.isGoal()){
				toFlip.setVisible(false);
				System.out.println("J'ai trouvé un but");
				if(toFlip.hasGold()){
					System.out.println("On a trouvé Laure");
				}
			}
		}
	}

	public OperationPathCard setP(Position p) {
		this.p = p;
		return this;
	}
	
	
}
