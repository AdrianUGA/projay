package saboteur.model;

import java.util.ArrayList;

import saboteur.model.Card.*;

public class OperationPathCard extends Operation {
	private static final long serialVersionUID = -2843575527220989261L;
	private Position p; //TO SAVE
	private ArrayList<Position> goalCardsToFlip; //TO SAVE
	private boolean reversed;
	
	public Position getP() {
		return p;
	}

	public ArrayList<Position> getGoalCardsToFlip() {
		return goalCardsToFlip;
	}

	public OperationPathCard(Player sourcePlayer, Card card, Position position) {
		super(sourcePlayer, card);
		this.p = position;
		this.reversed = false;
	}

	@Override
	public void exec(Game game) {
		if (reversed){
			((PathCard) this.getCard()).reverse();
		}
		this.getSourcePlayer().removeHandCard(this.getCard());
		game.getBoard().addCard((PathCard)this.getCard(), p);
		
		this.goalCardsToFlip = game.getBoard().getGoalCardsToFlip((PathCard)this.getCard(), p);
		PathCard toFlip;
		for (Position p : this.goalCardsToFlip){
			toFlip = game.getBoard().getCard(p);
			if (toFlip.isGoal()){
				game.notifyAINoGoldThere(p);
				
				if (!toFlip.hasGold() && !game.getBoard().isPossible(toFlip, p)) toFlip.reverse();
				
				toFlip.setVisible(true);
			}
		}
		
		game.notify(this);
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
			}
		}
	}

	public OperationPathCard setP(Position p) {
		this.p = p;
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		OperationPathCard o = (OperationPathCard) obj;
		
		return super.equals(obj) && o.getP() == this.getP() && o.getReversed() == this.getReversed();
	}

	public boolean getReversed() {
		return this.reversed;
	}

	public OperationPathCard setReversed(boolean reversed) {
			this.reversed = reversed;
		return this;
	}
	
	@Override
	public boolean isOperationPathCard(){
		return true;
	}
	
	@Override
	public void displayOperationInformation(){
		System.out.print("OperationPathCard : Position cX = "+getP().getcX() + " cY = " + getP().getcY() + " " + ((PathCard) getCard()).toString());
	}
}
